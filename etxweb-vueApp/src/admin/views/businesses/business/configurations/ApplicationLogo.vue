<template>
  <configuration-container v-if="logoGroupConfiguration.dtype"
                           :title="'Application Logo'"
                           :edit-mode="editMode"
                           @edit="editMode = true"
                           @confirm="confirm"
                           @reset="reset">
    <div class="row">
      <label for="logoInputUpdate" class="col-lg-4 col-form-label">
        Logo (PNG)
        <more-information
          :content="'Only PNG images are accepted. For better results the image should have a minimum size of 400px wide or 400px high and less than 100KB.'"
          placement="right"/>
      </label>
      <div class="col-lg-8 d-flex align-items-center justify-content-between">
        <div v-if="editMode">
          <file-upload input-id="logoInputUpdate" class="btn btn-outline-primary btn-file-upload"
                       accept="image/png"
                       @input-file="logoInputUpdate">
            <span class="ico-conjunction">
              <i aria-hidden="true" class="ico-add-primary"></i>
              <span>Select a file</span>
              <i aria-hidden="true" class="ico-file-primary"></i>
            </span>
          </file-upload>
        </div>
        <div v-if="logoGroupConfiguration.stringValue">
          <strong>{{ filename }}</strong>
        </div>
        <div v-if="editMode && logoGroupConfiguration.stringValue">
          <button type="button" class="ico-standalone delete" v-tooltip.tooltip="'Delete'"
                  v-on:click="removeLogo">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </button>
        </div>
      </div>
    </div>

    <div class="modal fade" :class="{'show d-block': showPreview}" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-body bg-info">
            <h2 class="h5 mb-0 ico-pins-container justify-content-start p-0">
              <i aria-hidden="true" class="ico-info-primary me-1"></i><span>Logo Preview</span>
            </h2>

            <div class="header-main-logos d-block my-2 text-center">
              <img alt="preview" class="logo-ec" :src="tempUrl">
              <span>
              <a @click.prevent="" aria-label="European Commission eTrustEx" class="branding text-start"
                 style="vertical-align: middle">
                eTrustEx<br>Web
              </a>
            </span>
            </div>
          </div>

          <div class="modal-footer bg-info">
            <button type="button" class="btn btn-outline-secondary" v-on:click="showPreview = false">
              Cancel
            </button>
            <button type="button" class="btn btn-info" v-on:click="save(); $event.preventDefault();">
              Confirm
            </button>
          </div>
        </div>
      </div>
    </div>
  </configuration-container>
</template>
<script setup lang="ts">
import { computed, ComputedRef, PropType, readonly, ref, Ref } from 'vue'
import useDialogStore from '@/shared/store/dialog'
import { Group, GroupConfigurationSpec, LogoGroupConfiguration } from '@/model/entities'
import { GroupConfigurationApi } from '@/shared/api/groupConfigurationsApi'
import FileUpload from 'vue-upload-component'
import Modal from '@/shared/views/modal/Modal.vue'
import ConfigurationContainer from '@/admin/views/businesses/business/configurations/shared/ConfigurationContainer.vue'
import MoreInformation from '@/shared/views/MoreInformation.vue'
import { storeToRefs } from 'pinia'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { deepCopy } from '@/shared/views/bootstrap_vue/utils/object'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const dialogStore = useDialogStore()

const showPreview = ref(false)
const editMode = ref(false)

const { logoGroupConfiguration } = storeToRefs(useBusinessConfigurationStore())
let logoGroupConfigurationCopy = readonly(deepCopy(logoGroupConfiguration.value))

const tempUrl = ref('')
const fileName = ref('')
const previewDialog: Ref<typeof Modal | null> = ref(null)
const filename: ComputedRef<string> = computed(() => (`${props.business.name}_logo.png`.toLowerCase()))

async function save() {
  const spec = new GroupConfigurationSpec<string>()
  spec.active = logoGroupConfiguration.value.active
  spec.value = logoGroupConfiguration.value.stringValue

  logoGroupConfiguration.value = await GroupConfigurationApi.updateConfiguration<string, LogoGroupConfiguration>(
    logoGroupConfiguration.value, spec
  )
  logoGroupConfigurationCopy = readonly(deepCopy(logoGroupConfiguration.value))

  editMode.value = false
  showPreview.value = false
  tempUrl.value = ''
}

function confirm() {
  console.log(tempUrl.value)
  if (tempUrl.value) {
    showPreview.value = true
  } else {
    save()
  }
}

function reset() {
  logoGroupConfiguration.value = deepCopy(logoGroupConfigurationCopy) as LogoGroupConfiguration
  editMode.value = false
  showPreview.value = false
  tempUrl.value = ''
}

function logoInputUpdate(target: any) {
  if ((target.file.size / 1024 / 1024 / 1024) > 2) { // 2GB limit
    dialogStore.show('Cannot save the file!', '<p class="mt-3">The file is too big.</p><p class="mt-3"> Please contact support</p>')
    return
  }
  if (!isPng(target.file)) {
    dialogStore.show('Cannot save the file!', '<p class="mt-3">The file could not be saved. Only PNG format is allowed.</p><p class="mt-3"> Please check the file format</p>')
    return
  }

  fileName.value = target.file.name
  const reader = new FileReader()
  reader.onloadend = () => {
    logoGroupConfiguration.value.stringValue = reader.result as string
    logoGroupConfiguration.value.active = true

    tempUrl.value = URL.createObjectURL(target.file)
    previewDialog.value?.show()
  }
  reader.readAsDataURL(target.file)
}

function removeLogo() {
  logoGroupConfiguration.value.stringValue = ''
  logoGroupConfiguration.value.active = false
}

function isPng(file: File) {
  const extension = file.name.substring((file.name.lastIndexOf('.') + 1))
  return extension && extension.toLowerCase() === 'png'
}

</script>
