<template>
  <div v-if="isSystemAdmin">
    <div class="d-flex justify-content-end">
      <button class="btn btn-outline-primary btn-collapse-arrow" type="submit"
              data-bs-toggle="collapse"
              data-bs-target="#groupForm">
        <span class="ico-conjunction">
          <i aria-hidden="true" class="ico-add-primary"></i>
          Add business
          <span aria-hidden="true" class="collapse-arrow"></span>
        </span>
      </button>
    </div>

    <div id="groupForm" class="collapse collapse-content-box" ref="collapseElement">
      <group-form ref="formGroup" :groupType="groupType" :parentGroupId="authenticationStore.rootGroupId"
                  @on-save="saveBusiness"/>
    </div>
  </div>

  <group-list :LIST_ITEM_ROUTE="BUSINESS" :groupType="groupType" :paginationCommand="paginationCommand"/>
</template>

<script setup lang="ts">
import { computed, ComputedRef, onMounted, Ref, ref } from 'vue'
import { GroupSpec, GroupType } from '@/model/entities'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import useGroupStore from '@/admin/store/group'
import useAuthenticationStore from '@/shared/store/authentication'
import GroupForm from '@/admin/views/shared/group/GroupForm.vue'
import GroupList from '@/admin/views/shared/group/GroupList.vue'
import { GroupApi } from '@/shared/api/groupApi'
import { Collapse } from 'bootstrap'

const groupStore = useGroupStore()
const authenticationStore = useAuthenticationStore()
const BUSINESS = { name: AdminRouteNames.BUSINESS }
const groupType = GroupType.BUSINESS
const requestParams = (authenticationStore.isSysAdmin() || authenticationStore.isOfficialInCharge())
  ? { groupType, parentId: authenticationStore.rootGroupId }
  : { groupIds: authenticationStore.getBusinessIds() }

const formGroup: Ref<typeof GroupForm | null> = ref(null)
const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => groupStore.fetchGroups(paginationOptions),
  { sortBy: 'auditingEntity.createdDate' },
  requestParams
)

const isSystemAdmin: ComputedRef<boolean> = computed(() => authenticationStore.isSysAdmin())

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
})

function saveBusiness(group: GroupSpec) {
  GroupApi.create(group)
    .then(() => paginationCommand.fetch({ page: 0 }))
    .then(() => {
      formGroup.value?.reset()
      collapseInstance.hide()
    })
}
</script>
