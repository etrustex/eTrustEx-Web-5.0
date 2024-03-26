<template>
  <div class="d-flex justify-content-end">
    <button data-bs-toggle="collapse" data-bs-target="#bulk-upload-form"
            class="btn btn-outline-primary btn-collapse-arrow" type="submit">
      <span>{{ toTitleCase(modelPlural) }} bulk creation</span>
      <span aria-hidden="true" class="collapse-arrow"></span>
    </button>
  </div>

  <div id="bulk-upload-form" class="collapse collapse-content-box" ref="collapseElement">
    <div class="container mt-3 form-admin-edit-mode">
      <div class="row">

        <label class="col-lg-6 col-form-label">{{ toTitleCase(modelPlural) }} csv file<span
          class="label-required">* </span>
          <more-information :content="moreInformationContent" placement="right"/>
        </label>

        <div class="col-lg-6">

          <file-upload
            ref="upload"
            :directory="false"
            :drop="false"
            :drop-directory="false"
            :multiple="false"
            :value="files"
            class="btn btn-outline-primary btn-file-upload btn-ico"
            input-id="file-upload"
            @input-file="inputUpdate"
          >
            <span class="ico-standalone"><i aria-hidden="true" class="ico-add-primary"></i></span>
            <span class="ms-0">Select a file</span>
            <span class="ico-standalone ms-1"><i aria-hidden="true" class="ico-file-primary"></i></span>
          </file-upload>

        </div>

      </div>

      <div class="row">
        <label class="col-lg-6 col-form-label">
          File name
        </label>
        <div class="col-lg-6">
          <strong class="text-break">{{ selectedFile.name }}</strong>
        </div>
      </div>

      <div class="row justify-content-end">
        <div class="col-lg-6">
          <button class="btn btn-primary btn-ico" data-test="uploadButton" @click="addItems()">
            <span class="ico-standalone"><i aria-hidden="true" class="ico-add-white"></i></span>
            <span class="ms-0">Add {{ modelPlural }}</span>
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, PropType, ref } from 'vue'
import FileUpload from 'vue-upload-component'
import { PaginationCommand } from '@/utils/pagination/paginationCommand'
import { toTitleCase } from '@/utils/formatters'
import useDialogStore from '@/shared/store/dialog'
import useGroupStore from '@/admin/store/group'
import useValidationStore from '@/shared/store/validation'
import { Collapse } from 'bootstrap'
import MoreInformation from '@/shared/views/MoreInformation.vue'

const props = defineProps({
  paginationCommand: { type: Object as PropType<PaginationCommand>, required: true },
  uploadFunction: {
    type: Function as PropType<(file: File, number: number) => Promise<Array<string>>>,
    required: true
  },
  sampleFileName: { type: String },
  model: { type: String },
  modelPlural: { type: String, required: true }
})

const dialogStore = useDialogStore()
const groupStore = useGroupStore()
const noFileChosen = 'No file chosen'
const selectedFile = ref<File>({ name: noFileChosen } as File)
const files = ref<Array<File>>([])

const moreInformationContent = computed(() => {
  const url = window.location.origin + window.location.pathname.replace(/\/?$/, '/')
  return 'You can find details about the ' + props.model + ' bulk creation on the Business Admin Guide or you can download the file below for instructions:\n\n' +
    '<br/><a href="' + url + 'bulk_samples/' + props.sampleFileName + '.xlsx" class="underline-on-hover">' + props.sampleFileName + ' (XLSX)</a>'
})

const collapseElement = ref<HTMLElement>()
let collapseInstance: Collapse = {} as Collapse

onMounted(() => {
  collapseInstance = new Collapse(collapseElement.value as HTMLElement, { toggle: false })
})

function inputUpdate(target: any) {
  selectedFile.value = target.file
}

function addItems() {
  if (isCSVFile()) {
    return props.uploadFunction(selectedFile.value, groupStore.business.id)
      .then(response => {
        if (response.length > 0) {
          useValidationStore()
            .setTitle(`The ${props.modelPlural} from the file could not be added due to the following error(s):`)
          useValidationStore()
            .setServerSideValidationErrors(response)
          reset()
        } else {
          dialogStore.show(`Add ${props.model} bulk:`, `${toTitleCase(props.modelPlural)} have been successfully added`)
          props.paginationCommand.fetch()
            .then(() => {
              reset()
            })
        }
      })
      .catch(reason => {
        console.error(reason)
      })
  } else {
    if (selectedFile.value.name === noFileChosen) {
      dialogStore.show('No file selected!', 'Please select a csv file')
    } else {
      dialogStore.show('Cannot open the file!', 'The file cannot be opened, please check the file format')
      reset()
    }
  }
}

function isCSVFile() {
  const extension = selectedFile.value.name.substring(selectedFile.value.name.lastIndexOf('.') + 1, selectedFile.value.name.length)
  return extension === 'csv' || extension === 'CSV'
}

function reset() {
  selectedFile.value = { name: noFileChosen } as File
  files.value = []
  collapseInstance.hide()
}

</script>
