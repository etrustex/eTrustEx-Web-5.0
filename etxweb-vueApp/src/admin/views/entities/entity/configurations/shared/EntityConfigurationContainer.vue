<template>
  <div class="container my-5">
    <div class="d-flex justify-content-between">
      <h4>{{ title }}</h4>
      <div class="d-flex align-items-md-end">
        <more-information v-if="showInfoButton && infoContent" :content="infoContent" class="my-auto" placement="left"/>
        <button v-if="!hideEditButton" type="button" class="ico-standalone" v-on:click="emit('edit')">
          <i class="ico-edit"></i>
        </button>
      </div>
    </div>

    <form class="form-admin" v-bind:class="(editMode)?'form-admin-edit-mode':'form-admin-view-mode'">
      <div class="container">
        <slot></slot>
        <div v-if="editMode" class="row">
          <div class="col-lg-12 text-end btns">
            <button type="button" class="btn btn-outline-secondary btn-ico" v-on:click="emit('reset')">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-cancel"></i></span>
              <span>Cancel</span>
            </button>
            <button type="button" class="btn btn-primary btn-ico" :disabled="disableConfirm"
                    v-on:click="emit('confirm')">
              <span class="ico-standalone"><i aria-hidden="true" class="ico-save-white"></i></span>
              <span>Save</span>
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  title: { type: String, required: true },
  confirmText: { type: String, required: false, default: 'Save' },
  infoContent: { type: String, required: false },
  editMode: { type: Boolean, required: true },
  showInfoButton: { type: Boolean, required: false, default: false },
  hideEditButton: { type: Boolean, required: false, default: false },
  disableConfirm: { type: Boolean, required: false, default: false }
})
const emit = defineEmits(['edit', 'reset', 'confirm'])
</script>
