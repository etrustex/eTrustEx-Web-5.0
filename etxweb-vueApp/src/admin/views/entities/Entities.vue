<template>
  <div class="d-flex justify-content-end">
    <button v-if="showAddEntities" class="btn btn-outline-primary btn-collapse-arrow" type="submit"
            data-bs-toggle="collapse"
            data-bs-target="#groupForm">
      <span class="ico-conjunction">
        <i aria-hidden="true" class="ico-add-primary"></i>
        Add entity
        <span aria-hidden="true" class="collapse-arrow"></span>
      </span>
    </button>
  </div>

  <div id="groupForm" class="collapse collapse-content-box" ref="collapseElement">
    <group-form v-if="showAddEntities" ref="formGroup" :groupType="groupType" :parentGroupId="businessId"
                @on-save="saveEntity"/>
  </div>

  <div class="mt-3">
    <bulk-upload v-if="showAddEntities" :paginationCommand="paginationCommand" :uploadFunction="uploadFunction"
                 :sampleFileName="'entities_bulk_creation'" :model="'entity'" :modelPlural="'entities'">
    </bulk-upload>
  </div>

  <search searchBy="Name or Identifier" searchTooltip="Search groups"
          :filterFn="filterGroups" :prefilterFn="prefilterFn"/>

  <group-list :LIST_ITEM_ROUTE="entityRoute" :groupType="groupType" :paginationCommand="paginationCommand"/>
</template>

<script lang="ts" setup>
import { GroupSpec, GroupType, SearchItem } from '@/model/entities'
import { computed, onMounted, Ref, ref } from 'vue'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import useGroupStore from '@/admin/store/group'
import { GroupApi } from '@/shared/api/groupApi'
import useAuthenticationStore from '@/shared/store/authentication'
import GroupForm from '@/admin/views/shared/group/GroupForm.vue'
import BulkUpload from '@/admin/views/shared/BulkUpload.vue'
import Search from '@/shared/views/Search.vue'
import GroupList from '@/admin/views/shared/group/GroupList.vue'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'
import { useRoute } from 'vue-router'
import { Collapse } from 'bootstrap'

const authenticationStore = useAuthenticationStore()
const groupStore = useGroupStore()
const entityRoute = { name: AdminRouteNames.ENTITY }
const groupType = GroupType.ENTITY
const route = useRoute()
const businessId: number = +route.params.businessId

const showAddEntities = computed(() => authenticationStore.isBusinessAdmin(businessId) || authenticationStore.isSysAdmin())
const uploadFunction = (file: File, groupId: number) => GroupApi.uploadBulk(file, groupId)

const formGroup: Ref<typeof GroupForm | null> = ref(null)
const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

const paginationCommand: PaginationCommand = new PaginationCommand(
  (paginationOptions: PaginationOptions) => groupStore.fetchGroups(paginationOptions),
  { sortBy: 'auditingEntity.createdDate' },
  { groupType, parentId: businessId || '' }
)

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
})

function filterGroups(filterValue: string) {
  paginationCommand.options.filterBy = 'nameOrIdentifierInput'
  paginationCommand.options.filterValue = filterValue
  paginationCommand.fetch({ page: 0 })
    .then()
}

async function prefilterFn(searchBy: string) {
  const items = await GroupApi.search(businessId, searchBy)
  return items.map(i => ({
    value: `${i.identifier} - ${i.name}`,
    searchValue: i.identifier
  } as SearchItem))
}

function saveEntity(group: GroupSpec) {
  GroupApi.create(group)
    .then(() => paginationCommand.fetch({ page: 0 }))
    .then(() => {
      formGroup.value?.reset()
      collapseInstance.hide()
    })
}
</script>
