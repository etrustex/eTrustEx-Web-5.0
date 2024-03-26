<template>
    <div class="modal fade" :id="id" tabindex="-1" aria-labelledby="" aria-hidden="true" ref="modalElement">
      <div class="modal-dialog" :class="modalClasses">
        <div class="modal-content" :class="contentClasses">
          <div v-if="slots.header" class="modal-header">
            <slot name="header" />
          </div>
          <div v-if="slots.body" class="modal-body" :class="bodyClasses">
            <slot name="body" />
          </div>
          <div v-if="slots.footer" class="modal-footer" :class="footerClasses">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">

import { onMounted, ref, useSlots } from 'vue'
import { Modal } from 'bootstrap'
import ClassValue from '@/shared/types/ClassValue'

interface ModalProps {
  id?: string
  title?: string
  modalClasses?: ClassValue
  contentClasses?: ClassValue
  bodyClasses?: ClassValue
  footerClasses?: ClassValue
}

const props = withDefaults(defineProps<ModalProps>(), {
  title: ''
})

const modalElement = ref<HTMLElement>()
let modalInstance: Modal = {} as Modal
const slots = useSlots()

onMounted(() => {
  modalInstance = new Modal(modalElement.value as HTMLElement)
})

const show = () => {
  modalInstance.show()
}

const hide = () => {
  modalInstance.hide()
}

defineExpose({ show, hide })
</script>
