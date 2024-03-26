<template>
  <div>
    <div v-if="messageBody">
      <div class="text-center">
        <h3 class="variant">Message</h3>
      </div>

      <div v-if="messageBody.length < 300">
        <!-- @formatter:off -->
        <pre class="message-detail-message">{{ messageBody }}</pre>
        <!-- @formatter:on -->
      </div>

      <div v-else>
        <!-- @formatter:off -->
        <pre class="message-detail-message">
          <span>{{ messageBody.substring(0, 300) }}</span><span class="collapse" id="collapsedTextMessage">{{ messageBody.substring(300) }}</span><a class="text-primary" data-bs-toggle="collapse" href="#collapsedTextMessage" role="button" aria-expanded="false" aria-controls="collapsedText"><span class="collapsed-no text-nowrap"> show less...</span><span class="collapsed-yes text-nowrap"> show more...</span></a>
        </pre>
        <!-- @formatter:on -->
      </div>

    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'MessageDetailsBody',
  props: {
    messageBody: { type: String }
  }
})
</script>
