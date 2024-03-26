<template>
  <div class="d-flex justify-content-between align-items-center">
    <div v-if="editMode" class="col-5">
      <file-upload input-id="file-upload" class="btn btn-outline-primary btn-file-upload btn-ico"
                   aria-label="Add files"
                   :directory="false" :drop="false" :drop-directory="false" :multiple="false"
                   :value="files"
                   @input-file="setFile">
        <span class="ico-standalone"><i aria-hidden="true" class="ico-add-primary"></i></span>
        <span class="ms-0">Select a file</span>
        <span class="ico-standalone ms-1"><i aria-hidden="true" class="ico-file-primary"></i></span>

      </file-upload>
    </div>

    <div class="col-5">
      <strong v-if="publicKeyFileName">{{ publicKeyFileName }}</strong>
      <strong v-else>No public key</strong>
    </div>

    <div v-if="editMode" class="col-1" v-tooltip.tooltip="'Delete'">
      <a href="javascript:void(0);"
         class="ico-standalone delete"
         type="button"
         @click.prevent="removePublicKey()"
         @keydown.enter.prevent="onEnter">
        <i class="ico-delete"></i>
        <span>Remove</span>
      </a>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref, Ref } from 'vue'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { RSA } from '@/utils/crypto/rsa'
import useCertificateStore from '@/shared/store/certificate'
import useGroupStore from '@/admin/store/group'
import FileUpload from 'vue-upload-component'
import { formatBytes } from '@/utils/formatters'

const emit = defineEmits(['updated'])

const props = defineProps({
  publicKey: { type: String },
  publicKeyFileName: { type: String },
  editMode: { type: Boolean },
  ignoreStore: { type: Boolean, default: false },
})

const dialogStore = useDialogStore()
const groupStore = useGroupStore()
const MAX_FILE_SIZE = 102400
const certificateStore = useCertificateStore()
const files: Ref<Array<File>> = ref([])

onMounted(() => {
  if (!props.ignoreStore) {
    emit('updated', {
      key: groupStore.entity.recipientPreferences?.publicKey ? groupStore.entity.recipientPreferences.publicKey : '',
      name: groupStore.entity.recipientPreferences?.publicKeyFileName ? groupStore.entity.recipientPreferences.publicKeyFileName : 'No file chosen',
    })
  }
})

function setFile(target: any) {
  const { file } = target

  if (MAX_FILE_SIZE < certificateStore.fileSize) {
    dialogStore.show('Public key size limit error', `<p><strong>The public key file cannot exceed ${ formatBytes(MAX_FILE_SIZE) }</strong></p>`, DialogType.ERROR)
  }

  Promise.all([getPublicKeyFromFile(file), getRawKeyFromFile(file)]).then(([pubKey, rawPubKey]) => {
    emit('updated', { key: pubKey, name: file.name, rawKey: rawPubKey })
  })
}

async function getPublicKeyFromFile(file: File) {
  const buffer: ArrayBuffer = await file.arrayBuffer()
  const bytes: Uint8Array = new Uint8Array(buffer)
  return RSA.parsePublicKey(bytes)
}

async function getRawKeyFromFile(file: File) {
  const buffer: ArrayBuffer = await file.arrayBuffer()
  const bytes: Uint8Array = new Uint8Array(buffer)
  return RSA.parseRawPublicKey(bytes)
}

function removePublicKey() {
  emit('updated', { key: '', name: '', rawKey: '' })
}

</script>
