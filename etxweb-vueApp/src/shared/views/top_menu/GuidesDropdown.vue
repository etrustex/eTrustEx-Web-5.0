<template>
  <div class="dropdown">
    <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown"
            aria-expanded="false">
      <span class="ico-standalone"><i class="ico-help"></i></span>
    </button>
    <ul class="dropdown-menu">
      <li>
        <a class="dropdown-item" :href="`mailto:${supportEmail}`">Support Contact</a>
      </li>
      <li v-for="guide in userGuides" :id="guide.id" :key="guide.id">
        <a class="dropdown-item" v-on:click="getUserGuideFile(guide)">{{ guide.label }}</a>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import application from '@/application.json'
import { AdminGuide } from '@/model/guides'
import { GroupType, RoleName } from '@/model/entities'
import useAuthenticationStore from '@/shared/store/authentication'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { UserGuideApi } from '@/shared/api/userGuideApi'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'

export default defineComponent({
  name: 'GuidesDropdown',
  computed: {
    supportEmail: () => {
      const { supportEmailGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
      return supportEmailGroupConfiguration.value.stringValue
        ? supportEmailGroupConfiguration.value.stringValue
        : application.supportEmail.toString()
    }
  },
  setup() {
    const authenticationStore = useAuthenticationStore()
    const currentGroupStore = useCurrentGroupStore()

    const currentGroup = computed(() => currentGroupStore.group)
    const userGuides = computed(() => {
      const guides: Array<AdminGuide> = [{
        label: 'User Guide (PDF)',
        role: RoleName.OPERATOR,
        groupType: GroupType.ENTITY
      }]

      const isEntityAdmin: boolean = authenticationStore.isEntityAdmin(currentGroup.value.id)
      const isBusinessAdmin: boolean = authenticationStore.isBusinessAdmin(currentGroup.value.businessId)
      const isSystemAdmin: boolean = authenticationStore.isSysAdmin()

      if (isEntityAdmin || isBusinessAdmin || isSystemAdmin) {
        guides.push({
          label: 'Entity Admin Guide (PDF)',
          role: RoleName.GROUP_ADMIN,
          groupType: GroupType.ENTITY
        })
      }

      if (isBusinessAdmin || isSystemAdmin) {
        guides.push({
          label: 'Business Admin Guide (PDF)',
          role: RoleName.GROUP_ADMIN,
          groupType: GroupType.BUSINESS
        })
      }

      return guides
    })

    function getUserGuideFile(adminGuide: AdminGuide) {
      const { role, groupType } = adminGuide
      const businessId = currentGroup.value.businessId ? currentGroup.value.businessId : authenticationStore.userDetails.roles[0].businessId
      return UserGuideApi.get({ role, groupType, businessId })
    }

    return {
      userGuides, getUserGuideFile
    }
  }
})
</script>
