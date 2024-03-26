<template>
  <div class="message-detail-header">
    <div v-if="message.highImportance" class="mb-2">
      <img style="max-width: 1.5rem" class="ico-invalid-certificate" src="@/assets/ico/rounded-error-danger.svg"
           alt="invalid certificate">
      <span class="text-danger font-weight-bold">This message was sent with high importance</span>
    </div>

    <div class="d-flex align-self-center">
      <p class="mb-2">
        <span class="message-detail-date">
          {{ date }}
        </span>
      </p>

      <div class="d-flex me-0 ms-auto">
        <signature-validation v-if="messageSummary"/>
        <button v-if="includeReply" class="btn btn-outline-primary btn-ico" @click="$emit('reply')">
          <span class="ico-standalone"><i aria-hidden="true" class="ico-reply-primary"></i></span>
          <span>Reply</span>
        </button>
      </div>
    </div>

    <div v-if="message.subject.length < 110">
      <h2 class="message-detail-subject text-break">{{ message.subject }}</h2>
    </div>

    <div v-else>
      <h2 class="message-detail-subject text-break">
        <span>{{ message.subject.substring(0, 110) }}</span>

        <span class="collapse" id="collapsedTextTitle">
          {{ message.subject.substring(110) }}
        </span>

        <a class="text-primary" data-bs-toggle="collapse" href="#collapsedTextTitle" role="button"
           aria-expanded="false" aria-controls="collapsedText">
          <span class="collapsed-no text-nowrap"> show less...</span>
          <span class="collapsed-yes text-nowrap"> show more...</span>
        </a>
      </h2>
    </div>

    <slot></slot>

    <ul class="message-summary-content d-none">
      <li style="visibility: hidden">
        <span class="ico-conjunction"><i title="Help" class="ico-https"></i> Encrypted</span>
      </li>
      <li style="visibility: hidden">
        <span class="ico-conjunction"> <i title="Signed" class="ico-done"></i> Signed</span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Message, MessageSummary } from '@/model/entities'
import { formatDate } from '@/utils/formatters'
import { defineComponent, PropType } from 'vue'
import SignatureValidation from '@/messages/views/details/SignatureValidation.vue'

export default defineComponent({
  name: 'MessageDetailsHeader',
  components: {
    SignatureValidation
  },
  emits: ['reply'],
  props: {
    messageSummary: { type: Object as PropType<MessageSummary | null> },
    message: { type: Object as PropType<Message> },
    includeReply: { type: Boolean },
    date: { type: String }
  },
  setup() {
    return { formatDate }
  }
})
</script>
