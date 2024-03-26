<template>
  <div v-if="template">
    <v-runtime-template :template="template" :templateVariables="templateVariables" :key="runtimeTemplateKey"
                        :template-props="templateProps">
    </v-runtime-template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { formatListForDisplay } from '@/utils/stringListHelper'
import VRuntimeTemplate from 'vue3-runtime-template'
import { storeToRefs } from 'pinia'
import useMessageFormStore from '@/messages/store/messageForm'

const props = defineProps({
  templateVariables: { type: Object, default: null },
  template: { type: String },
  v$: { type: Object }
})

const messageFormStore = useMessageFormStore()
const { message, templateVariables: messageTemplateVariables } = storeToRefs(messageFormStore)
const templateVariables = ref(props.templateVariables ?? messageTemplateVariables)
const runtimeTemplateKey = ref(0)

const v$ = ref(props.v$)

const onTemplateRecipientsSaved = () => {
  if (templateVariables.value.emailRecipients) {
    messageTemplateVariables.value = {
      ...templateVariables.value,
      emailRecipients: formatListForDisplay(templateVariables.value.emailRecipients)
    }
  }
}

const templateProps = {
  onTemplateRecipientsSaved
}

</script>
