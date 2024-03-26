<template>
  <b-table
    :fields="fields"
    :items="groups"
    :no-local-sorting="true"
    @sort-changed="sortingChanged"
    sort-icon-left
    class="mt-3"
  >
    <template #cell(identifier)="data">
      <router-link v-if="authenticationStore.isAnAdmin()" :to="getItemRoute(data.item.id)">
        <span class="text-break">{{ data.item.identifier }}</span>
      </router-link>
      <span v-else>{{ data.item.identifier }}</span>
    </template>
    <template #cell(name)="data">
      <span class="text-break">{{ data.item.name }}</span>
    </template>
    <template #cell(isSystem)="data">
      <span v-if="data.item.isSystem" class="badge rounded-pill bg-primary ms-3">Back-end system</span>
    </template>
    <template v-if="groupType === ENTITY" #cell(encrypted)="data">
        <span v-if="isEncrypted(data.item.recipientPreferences) && data.item.recipientPreferences.publicKey"
              class="ico-conjunction"><i class="ico-encrypted-mandatory"
                                         title="Encrypted"></i></span>
      <span v-else-if="data.item.recipientPreferences && data.item.recipientPreferences.publicKey"
            class="ico-conjunction"><i class="ico-encrypted"
                                       title="Encrypted"></i></span>
      <span v-else class="ico-conjunction"><i class="ico-not-encrypted" title="Not encrypted"></i></span>
    </template>
    <template #cell(status)="data">
      <div class="form-check form-switch form-control-lg py-0">
        <input type="checkbox" role="switch" class="form-check-input" name="'check-button'" disabled
               v-model="data.item.active">
      </div>
    </template>
    <template v-if="displayActionsColumn" #cell(actions)="data">
      <ul class="ico-list">
        <li v-if="data.item.pendingDeletion">
          <a class="ico-standalone" href="javascript:void(0);" type="button" v-tooltip.tooltip="'Edit'"
             v-on:click="data.toggleDetails">
            <i class="ico-edit"></i>
            <span>Edit</span>
          </a>
        </li>
        <li>
          <a v-if="data.item.type === ENTITY || (!data.item.pendingDeletion && authenticationStore.isSysAdmin())"
             class="ico-standalone delete" href="javascript:void(0);" type="button"
             v-tooltip.tooltip="'Delete'"
             v-on:click="confirmDeleteGroup(data.item)">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </li>
        <li>
          <a v-if="data.item.pendingDeletion" class="ico-standalone delete" href="javascript:void(0);" type="button"
             v-tooltip.tooltip="'Pending'">
            <i class="ico-pending-delete"></i>
            <span>Pending</span>
          </a>
        </li>
      </ul>
    </template>
    <template #cell(more)="row">
      <a :class="`ico-standalone has-tooltip ${row.item._showDetails ? 'not-collapsed': 'collapsed'}`" type="button"
         v-on:click="row.toggleDetails">
        <i class="ico-keyboard-arrow-down"></i><span>See overview</span>
      </a>
    </template>
    <template #row-details="row">
      <group-details :group-detail="row.item"
                     @cancel-deletion="deleteBusinessCancellation(row.item)"
                     @confirm-deletion="deleteBusinessConfirmation(row.item)"/>
    </template>
  </b-table>

  <div class="container">
    <div class="row">
      <div class="col mt-3">
        <pagination :page="groupsPage" :paginationCommand="paginationCommand"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ComputedRef, onMounted, PropType } from 'vue'
import {
  Confidentiality,
  Direction,
  Group,
  GroupType,
  RecipientPreferences,
  RestResponsePage,
  RoleName,
} from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { RouteLocation, useRoute } from 'vue-router'
import useGroupStore from '@/admin/store/group'
import GroupDetails from '@/admin/views/shared/group/GroupDetails.vue'
import useAuthenticationStore from '@/shared/store/authentication'
import { GroupApi } from '@/shared/api/groupApi'
import Pagination from '@/shared/views/Pagination.vue'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'
import useDialogStore, { buttonWithCallback, CANCEL_BUTTON, DialogType } from '@/shared/store/dialog'
import { UserProfileApi } from '@/admin/api/userProfileApi'

const props = defineProps({
  groupType: { type: String as PropType<GroupType> },
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
  LIST_ITEM_ROUTE: { type: Object as PropType<Partial<RouteLocation>>, required: true }
})

const groupStore = useGroupStore()
const { BUSINESS } = GroupType
const { ENTITY } = GroupType
const fields = [
  { key: 'identifier', thClass: 'w-25', tdClass: 'w-25', sortable: true },
  { key: 'name', thClass: 'w-25', tdClass: 'w-25', sortable: true },
  ...props.groupType === ENTITY ? [{ key: 'isSystem', label: 'System connection', sortable: true }] : [],
  ...props.groupType === ENTITY ? [{ key: 'status' }] : [],
  ...props.groupType === ENTITY ? [{ key: 'encrypted', label: 'End-to-end encryption' }] : [],
  { key: 'more' }
]

const dialogStore = useDialogStore()
const authenticationStore = useAuthenticationStore()
const route = useRoute()
const businessId: number = +route.params.businessId
const displayActionsColumn: boolean = authenticationStore.isSysAdmin() || authenticationStore.isOfficialInCharge() || (props.groupType === ENTITY && authenticationStore.isBusinessAdmin(+route.params.businessId))
const groupsPage: ComputedRef<RestResponsePage<Group>> = computed(() => groupStore.groupsPage)
const groups: ComputedRef<Array<Group>> = computed(() => groupStore.groups)

onMounted(() => {
  props.paginationCommand.fetch()
    .then()

  if (displayActionsColumn) {
    fields.splice(fields.length - 1, 0, { key: 'actions' })
  }
})

function isEncrypted(recipientPreferences: RecipientPreferences): boolean {
  return recipientPreferences ? recipientPreferences.confidentiality === Confidentiality.LIMITED_HIGH : false
}

function getItemRoute(itemId: number) {
  const params = BUSINESS === props.groupType
    ? { ...props.LIST_ITEM_ROUTE.params, businessId: itemId }
    : { ...props.LIST_ITEM_ROUTE.params, entityId: itemId }
  return { name: props.LIST_ITEM_ROUTE.name, params }
}

function confirmDeleteGroup(group: Group) {
  GroupApi.isEntityEmpty(group.id)
    .then(isEmpty => {
      const errorMessage = isEmpty ? 'The action cannot be undone.' : 'Be aware that if there are messages present, the entity will be disabled and removed from administration, but will only be completely removed once the messages reach the retention policy. Until the entity is completely removed, the name and identifiers cannot be reused.'

      dialogStore.show(
        `Delete ${ formatType(group.type) }`,
        `<p class="mt-3">Are you sure you want to delete the ${ formatType(group.type)
          .toLowerCase() } <strong class="text-break">${ group.identifier }</strong>?</p><p>${ errorMessage }</p>`,
        DialogType.ERROR,
        buttonWithCallback('Delete', () => group.type === GroupType.BUSINESS ? confirmDeleteBusiness(group) : deleteGroup(group.id)),
        CANCEL_BUTTON
      )
    })
}

function confirmDeleteBusiness(group: Group) {
  GroupApi.isBusinessEmpty(group.id)
    .then(isBusinessEmpty => {
      if (!isBusinessEmpty) {
        dialogStore.show(
          'Delete confirmation',
          `<p class="mt-3">If you delete the business <strong class="text-break">${ group.identifier }</strong> all data related to that business will be completely removed.</p><p>A validation request will be sent to the officials in charge.</p><p>Do you wish to continue?</p>`,
          DialogType.INFO,
          buttonWithCallback('Ok', () => deleteBusiness(group)),
          CANCEL_BUTTON
        )
      } else {
        deleteGroup(group.id)
      }
    })
}

function deleteBusiness(group: Group) {
  UserProfileApi.getListItems({
    role: RoleName.OFFICIAL_IN_CHARGE,
    groupId: useAuthenticationStore().rootGroupId
  }, true)
    .then(page => {
      if (page.totalElements === 0) {
        dialogStore.show('No officials in charge!', 'There are no officials in charge configured to authorize the deletion.')
        return
      }

      return GroupApi.deleteBusiness(group.id)
        .then(() => props.paginationCommand.fetch({
          page: props.paginationCommand
            .currentOrLast(groupStore.groupsPage.totalElements, groupStore.groupsPage.number)
        }))
    })
}

function deleteGroup(groupId: number) {
  return GroupApi.delete(groupId)
    .then(() => props.paginationCommand.fetch({
      page: props.paginationCommand
        .currentOrLast(groupStore.groupsPage.totalElements - 1, groupStore.groupsPage.number)
    }))
}

function deleteBusinessCancellation(group: Group) {
  dialogStore.show(
    'Cancel deletion',
    `<p class="mt-3">Are you sure you want to cancel the deletion of the business <strong class="text-break">${ group.identifier }</strong>?</p>`,
    DialogType.INFO,
    buttonWithCallback('Ok',
      () => GroupApi.cancelBusinessDeletion(group.id)
        .then(() => props.paginationCommand.fetch({
          page: props.paginationCommand
            .currentOrLast(groupStore.groupsPage.totalElements, groupStore.groupsPage.number)
        }))),
    CANCEL_BUTTON
  )
}

function deleteBusinessConfirmation(group: Group) {
  dialogStore.show(
    'Delete Business',
    `<p class="mt-3">If you confirm the deletion of the business <strong class="text-break">${ group.identifier }</strong> all data related to that business will be completely removed within 12 days.</p><p>Do you confirm the deletion?</p>`,
    DialogType.ERROR,
    buttonWithCallback('Delete',
      () => GroupApi.confirmBusinessDeletion(group.id)
        .then(() => props.paginationCommand.fetch({
          page: props.paginationCommand
            .currentOrLast(groupStore.groupsPage.totalElements, groupStore.groupsPage.number)
        }))),
    CANCEL_BUTTON
  )
}

async function sortingChanged(sortBy: string, isDesc: boolean) {
  props.paginationCommand.options.sortBy = sortBy ?? undefined
  props.paginationCommand.options.sortOrder = isDesc ? Direction.DESC : Direction.ASC
  await props.paginationCommand.fetch({ page: 0 })
    .then()
}

function formatType(type: GroupType) {
  switch (type) {
    case BUSINESS:
      return 'Business'
    case ENTITY:
      return 'Entity'
    case GroupType.ROOT:
      return 'Root Group'
  }
}
</script>
