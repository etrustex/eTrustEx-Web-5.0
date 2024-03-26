<template>
  <div class="container-fluid py-3">
    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">Identifier</div>
      <div class="col col-lg-6 mt-lg-2"><strong class="text-break">{{ groupDetail.identifier }}</strong></div>
    </div>

    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">Name</div>
      <div class="col col-lg-6 mt-lg-2"><strong class="text-break">{{ groupDetail.name }}</strong></div>
    </div>

    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">Description</div>
      <div class="col col-lg-6 mt-lg-2"><strong class="text-break">{{ groupDetail.description }}</strong></div>
    </div>

    <div class="row">
      <div class="col-8 col-lg-3 text-lg-end mt-2">Back-end system connection</div>
      <div class="col-8 col-lg-3 mt-lg-2">
        <img v-if="groupDetail.isSystem" alt="System Connection" style="width: 24px;"
             src="@/assets/ico/rounded-check-circle-success.svg">
      </div>
    </div>

    <div class="row">
      <div class="col-8 col-lg-3 text-lg-end mt-2">Channels</div>
      <div class="col-8 col-lg-3 mt-lg-2">
        <span v-for="(channel, index) in channels" :key="channel.id">
          <a class="underline-on-hover" href="javascript:void(0);" @click.prevent="goToChannel(channel.id)">{{ channel.name }}</a>
          <span v-if="index < (channels.length -1)">, </span>
        </span>
      </div>
    </div>

    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">Status</div>
      <div class="col col-lg-6 mt-lg-2">
        <strong>{{ status() }}</strong>
      </div>
    </div>

    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">Created on</div>
      <div class="col col-lg-6 mt-lg-2"><strong>{{ toDateTime(groupDetail.auditingEntity.createdDate) }}</strong></div>
    </div>

    <div class="row">
      <div class="col col-lg-3 text-lg-end mt-2">By</div>
      <div class="col col-lg-6 mt-lg-2"><strong>{{ groupDetail.auditingEntity.createdBy }}</strong></div>
    </div>

    <div
      v-if="(authenticationStore.isOfficialInCharge() || authenticationStore.isSysAdmin()) && groupDetail.pendingDeletion"
      class="d-flex justify-content-end mt-3">
      <div class="btns">
        <button v-if="authenticationStore.isOfficialInCharge() || authenticationStore.isSysAdmin()" type="button"
                class="btn btn-outline-secondary btn-ico" v-on:click="emits('cancel-deletion')">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
          <span>Cancel</span>
        </button>
        <button v-if="authenticationStore.isOfficialInCharge() && !groupDetail.removedDate" type="button"
                class="btn btn-primary btn-ico"
                v-on:click="emits('confirm-deletion')">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
          <span>Confirm</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, PropType, ref, Ref, watch } from 'vue'
import { Channel, Group } from '@/model/entities'
import useAuthenticationStore from '@/shared/store/authentication'
import { toDate, toDateTime } from '@/utils/formatters'
import { ChannelApi } from '@/admin/api/channelApi'
import { useRoute, useRouter } from 'vue-router'
import { AdminRouteNames } from '@/admin/router/adminRouteNames'

const emits = defineEmits(['cancel-deletion', 'confirm-deletion'])
const props = defineProps({
  groupDetail: { type: Object as PropType<Group>, required: true },
})

const authenticationStore = useAuthenticationStore()
const route = useRoute()
const router = useRouter()
const businessId: number = +route.params.businessId

const channels: Ref<Array<Channel>> = ref([])

onMounted(() => loadChannels())
watch(() => props.groupDetail, () => {
  loadChannels()
})

async function loadChannels() {
  channels.value = await ChannelApi.getChannelsByGroup(businessId, props.groupDetail.id)
}

function status() {
  if (props.groupDetail?.pendingDeletion) {
    if (props.groupDetail?.removedDate) {
      const date = new Date(props.groupDetail.removedDate)
      date.setDate(date.getDate() + 12)
      return `Pending deletion - ${ toDate(date) }`
    }
    return 'Pending deletion'
  }
  if (props.groupDetail?.active) {
    return 'Active'
  } else {
    return 'Inactive'
  }
}

function goToChannel(channelId: number) {
  router.push({ name: AdminRouteNames.BUSINESS_CHANNEL, params: { channelId: channelId.toString() } })
}

</script>
