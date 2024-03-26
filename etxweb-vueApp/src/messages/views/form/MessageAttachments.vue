<template>
  <div class="col-md-7 message-form-drag-drop" @dragover.prevent>
    <div class="example-drag sticky-top">
      <div class="upload">
        <div class="dropzone">
          <div class="ico-pins-container">
            <span class="ico-pins"></span>
            <i aria-hidden="true" class="ico-upload"></i>
          </div>
          <div class="dz-message">
            <h2 class="h3 variant">Drop file/folder</h2>
          </div>

          <file-upload ref="upload"
                       aria-label="Add files"
                       input-id="file-upload"
                       class="btn btn-outline-primary btn-ico mt-3"
                       :drop="true" :drop-directory="true"
                       :directory="false"
                       :multiple="true"
                       :value="attachmentSpecDtos"
                       @input-filter="inputFilter">
            <span aria-hidden="true" class="ico-standalone"><i class="ico-add-primary"></i></span>
            <span class="ms-0">Add files</span>
            <span aria-hidden="true" class="ico-standalone ms-1"><i class="ico-file-primary"></i></span>
          </file-upload>

          <button class="btn btn-outline-primary btn-ico mt-3 ms-3" href="javascript:void(0);" v-on:click="onAddFolder">
            <span aria-hidden="true" class="ico-standalone"><i class="ico-add-primary"></i></span>
            <span class="ms-0">Add folder</span>
            <span aria-hidden="true" class="ico-standalone ms-1"><i class="ico-folder-primary"></i></span>
          </button>

          <div class="mt-3">
            <strong>After adding the files, please click on "Upload files" button to submit them to the server.</strong>
          </div>
        </div>

        <transmission-limitation-info v-if="attachmentSpecDtos.length === 0"/>

        <div v-if="attachmentSpecDtos.length > 0" class="mt-3" data-test="filesListTable">

          <div class="d-flex justify-content-end align-items-center btns">

            <div v-if="showListView && someFilesSelected" class="dropdown">
              <button class="btn btn-outline-primary dropdown-toggle " type="button" data-bs-toggle="dropdown"
                      aria-expanded="false">Actions
              </button>
              <ul class="dropdown-menu">
                <li>
                  <a v-on:click="deleteSelectedFiles" class="dropdown-item">
                    <span class="ico-conjunction">
                      <i class="ico-delete"></i>
                      Delete files
                    </span>
                  </a>
                </li>
                <li v-if="showAdditionalAttachmentActions()">
                  <a v-on:click="toggleSelectedConfidential" class="dropdown-item">
                    <span class="ico-conjunction">
                      <i
                        :class="existsSelectedNotConfidential() ? 'ico-confidential-primary' : 'ico-not-confidential-primary'"/>
                      {{ (existsSelectedNotConfidential() ? 'Mark' : 'Unmark') + ' as confidential' }}
                    </span>
                  </a>
                </li>
              </ul>
            </div>

            <button v-if="showUploadButton" class="btn btn-primary btn-ico"
                    data-test="uploadButton"
                    type="button"
                    @click.exact="validateAttachmentsAndUpload()">
              <span class="ico-standalone">
                <i aria-hidden="true" class="ico-upload-white"></i>
              </span>
              <span>Upload files</span>
            </button>

            <button class="ico-standalone" type="button"
                    :title="showListView ? 'List view': 'Tree view'"
                    :aria-label="showListView ? 'See list view': 'See tree view'"
                    v-on:click="toggleAttachmentsView()">
              <span aria-hidden="true" class="ico-standalone">
                <i :class="showListView ? 'ico-tree-view-primary': 'ico-list-view-primary'"></i>
              </span>
            </button>

          </div>

          <upload-attachments-list-view v-if="showListView"
                                        :key="attachmentsViewsKey"
                                        :attachmentSpecDtos="attachmentSpecDtos"/>

          <upload-attachments-tree-view v-if="showTreeView"
                                        :key="attachmentsViewsKey"
                                        :attachmentSpecDtos="attachmentSpecDtos"/>

        </div>

        <label style="display: none">
          <input v-model="attachmentSpecDtos.length" type="text">
        </label>
        <span v-if="v$.attachmentSpecDtos.$error" class="error"> {{ v$.attachmentSpecDtos.$errors[0].$message }} </span>

      </div>
    </div>

  </div>
</template>

<script lang="ts">
import { computed, ComputedRef, defineComponent, onMounted, Ref, ref } from 'vue'
import FileUpload from 'vue-upload-component'
import UploadAttachmentsListView from '@/messages/views/form/attachments/UploadAttachmentsListView.vue'
import UploadAttachmentsTreeView from '@/messages/views/form/attachments/UploadAttachmentsTreeView.vue'
import useCurrentGroupStore from '@/shared/store/currentGroup'
import { AttachmentSpecWrapper, AttachmentStatus } from '@/model/attachmentDto'
import ProgressBar from '@/shared/views/ProgressBar.vue'
import useProgressTrackerStore from '@/messages/store/progress'
import TransmissionLimitationInfo from '@/messages/views/form/attachments/TransmissionLimitationInfo.vue'
import useMessageFormStore from '@/messages/store/messageForm'
import { storeToRefs } from 'pinia'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import useBusinessConfigurationStore from '@/shared/store/businessConfiguration'
import { formatBytes } from '@/utils/formatters'
import { AttachmentApi, attachmentUploadApi } from '@/messages/api/attachmentApi'

export default defineComponent({
  name: 'MessageAttachments',
  components: {
    TransmissionLimitationInfo,
    ProgressBar,
    UploadAttachmentsTreeView,
    UploadAttachmentsListView,
    FileUpload,
  },
  props: {
    e2eEnabled: { type: Boolean, required: true },
    v$: { type: Object, required: true },
  },
  setup(props) {
    const currentGroupStore = useCurrentGroupStore()
    const progressTrackerStore = useProgressTrackerStore()
    const dialogStore = useDialogStore()

    const messageFormStore = useMessageFormStore()
    const { message, attachmentsSymmetricKey, attachmentSpecDtos, fileNames } = storeToRefs(messageFormStore)

    const someFilesSelected: ComputedRef<boolean> = computed(() => attachmentSpecDtos.value.some(attachment => attachment.selected))
    const showListView = computed(() => (attachmentsView.value === 'LIST' && attachmentSpecDtos.value.length))
    const showTreeView = computed(() => (attachmentsView.value === 'TREE' && attachmentSpecDtos.value.length))
    const showUploadButton: ComputedRef<boolean> = computed(() =>
      !uploading.value && attachmentSpecDtos.value.some(attachmentSpecDto => attachmentSpecDto.status !== AttachmentStatus.Success))

    const attachmentsViewsKey: Ref<number> = ref(0)
    const attachmentsView: Ref<'LIST' | 'TREE'> = ref('LIST')

    const currentGroup = computed(() => currentGroupStore.group)

    const upload: Ref = ref(null)
    const directory: Ref<boolean> = ref(false) // used to let you pick a file or folder via a single button
    const uploading = ref(false)

    onMounted(() => {
      progressTrackerStore.$reset()
      attachmentSpecDtos.value = []
      fileNames.value.clear()
    })

    function existsSelectedNotConfidential() {
      return attachmentSpecDtos.value.find(attachment => attachment.selected && !attachment.attachmentSpec.confidential)
    }

    function showAdditionalAttachmentActions() {
      return currentGroupStore.group.parent?.additionalAttachmentActions
    }

    function toggleSelectedConfidential() {
      const value = !!existsSelectedNotConfidential()
      attachmentSpecDtos.value.filter(attachment => attachment.selected)
        .forEach(selectedAttachment => selectedAttachment.attachmentSpec.confidential = value)
      attachmentsViewsKey.value++
    }

    function toggleAttachmentsView() {
      attachmentsView.value = attachmentsView.value === 'LIST' ? 'TREE' : 'LIST'
    }

    function validateAttachmentLimits(totalBytes: number, attachmentSpecDtos: Array<AttachmentSpecWrapper>): boolean {
      const dialogStore = useDialogStore()
      const businessConfigurationStore = useBusinessConfigurationStore()

      const maxNumberOfFiles = businessConfigurationStore.numberOfFilesLimitationGroupConfiguration.active
        ? businessConfigurationStore.numberOfFilesLimitationGroupConfiguration.integerValue
        : 0
      const maxTotalFileSize = businessConfigurationStore.totalFileSizeLimitationGroupConfiguration.active
        ? businessConfigurationStore.totalFileSizeLimitationGroupConfiguration.integerValue * 1024 * 1024
        : 0

      let validAttachments = true

      if ((maxNumberOfFiles !== 0) && (attachmentSpecDtos.length > maxNumberOfFiles)) {
        dialogStore.show('Invalid attachments number', `The total number of attachments cannot exceed ${ maxNumberOfFiles } files`, DialogType.ERROR)
        validAttachments = false
      }

      if (validAttachments && (maxTotalFileSize !== 0) && (totalBytes > maxTotalFileSize)) {
        const message = `Maximum size of ${ formatBytes(maxTotalFileSize, true) } permitted`
        dialogStore.show('Invalid total size', message, DialogType.ERROR)
        validAttachments = false
      }

      return validAttachments
    }

    async function validateAttachmentsAndUpload() {
      const totalBytes = attachmentSpecDtos.value
        .filter(attachment => attachment.status === AttachmentStatus.Pending || attachment.status === AttachmentStatus.Error)
        .map(value => value.size)
        .reduce((previousValue, currentValue) => previousValue + currentValue, 0)

      if (validateAttachmentLimits(totalBytes, attachmentSpecDtos.value)) {
        progressTrackerStore.config = {
          title: progressTrackerStore.status === 'cancel' ? 'Cancelling...' : 'Uploading...',
          message: progressTrackerStore.status === 'cancel'
            ? 'Cancelling upload in progress. <br>This dialog will close automatically at the end of the process. '
            : 'Upload in progress.<br>This dialog will close automatically at the end of the upload.',
          isUpload: true,
          progressCancelFunction: cancelUpload,
        }
        progressTrackerStore.init(totalBytes)

        await AttachmentApi.removeFailedAttachments(attachmentSpecDtos.value)
        await AttachmentApi.createAndUploadAttachments(message.value, attachmentSpecDtos.value, attachmentsSymmetricKey.value, props.e2eEnabled)
        if (attachmentSpecDtos.value.some(attachment => attachment.status === AttachmentStatus.Error)) {
          progressTrackerStore.handleError({ name: 'UploadError', message: 'Some attachments failed to upload' })
        }
      }
    }

    async function cancelUpload() {
      attachmentUploadApi.abort()

      return new Promise<void>((resolve) => {
        setTimeout(function () {
          attachmentSpecDtos.value.forEach((attachmentSpecDto) => {
            if (attachmentSpecDto.status === AttachmentStatus.Error || attachmentSpecDto.status === AttachmentStatus.Uploading) {
              attachmentSpecDto.status = AttachmentStatus.Pending
            }
          })

          uploading.value = false
          resolve()
        }, 500)
      })
    }

    function inputFilter(newAttachmentSpecDto: AttachmentSpecWrapper, oldAttachmentSpecDto: AttachmentSpecWrapper, prevent: () => void) {
      if (progressTrackerStore.showProgress) {
        return prevent()
      }
      if (newAttachmentSpecDto && !oldAttachmentSpecDto) {
        if (newAttachmentSpecDto.size > 2e+9) {
          return prevent()
        }
      }

      messageFormStore.addAttachment(newAttachmentSpecDto)
    }

    function deleteSelectedFiles() {
      dialogStore.show(
        'Deleting attachments',
        '<p class="mt-3">Deleting attachments. This may take a while.</p>',
        DialogType.INFO,
        { show: false, title: '' },
      )
      messageFormStore.bulkDeleteAttachments(attachmentSpecDtos.value.filter(attachment => attachment.selected))
        .then(() => dialogStore.hide())
    }

    function onAddFolder() {
      if (!upload.value.features.directory) {
        alert('Your browser does not support folders')
        return
      }
      const fileUploadButton = upload.value.$el.querySelector('#file-upload')
      fileUploadButton.directory = true
      fileUploadButton.webkitdirectory = true
      directory.value = true
      fileUploadButton.onclick = null // disable onclick event on input so folders can be selected
      fileUploadButton.click() // auto click to open the select folder window
      // next time it's clicked, reset the params to handle files by default
      fileUploadButton.onclick = () => {
        directory.value = false
        fileUploadButton.directory = false
        fileUploadButton.webkitdirectory = false
      }
    }

    return {
      currentGroup,
      attachmentSpecDtos,
      attachmentsViewsKey,
      someFilesSelected,
      showListView,
      showTreeView,
      showUploadButton,
      toggleAttachmentsView,
      toggleSelectedConfidential,
      existsSelectedNotConfidential,
      showAdditionalAttachmentActions,
      inputFilter,
      onAddFolder,
      deleteSelectedFiles,
      validateAttachmentsAndUpload,
      upload,
    }
  },
})
</script>
