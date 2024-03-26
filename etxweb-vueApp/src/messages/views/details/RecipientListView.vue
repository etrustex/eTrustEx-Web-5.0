<template>
  <div class="message-detail-recipient">

    <div class="text-center">
      <h3 class="variant">List of recipients</h3>
    </div>

    <div class="mt-2 table-responsive message-detail-recipient-table">
      <table class="table align-middle">
        <thead class="thead-fixed">
          <tr>
            <th scope="col" class="col-5">Name</th>
            <th scope="col" class="col-2">Status</th>
            <th scope="col" class="col-2">Received on</th>
            <th scope="col" class="col-3">E2E encryption</th>
          </tr>
        </thead>
        <tbody class="table-striped">
          <tr v-for="recipient in recipients">
            <td class="overflow"><span>{{ recipient.name }}</span></td>
            <td><span class="ms-0 badge badge rounded-pill bg-primary">{{recipient.status}}</span></td>
            <td><span class="message-detail-date">{{ formatDate(recipient.statusModifiedDate) }}</span></td>
            <td><span v-if="recipient.e2eEncryption" class="ico-conjunction"><i class="ico-encrypted-mandatory" title="Encrypted"></i></span></td>
          </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { formatDate } from '@/utils/formatters'
import RecipientDto from '@/model/recipientDto'
import BTable from '@/shared/views/bootstrap_vue/BTtable/BTable.vue'

export default defineComponent({
  name: 'RecipientListView',
  props: {
    recipients: { type: Array as PropType<Array<RecipientDto>> }
  },
  components: {
    BTable
  },
  setup(props) {
    const TABLE_FIELDS = [
      { key: 'name', sortable: false, thClass: 'col-5', tdClass: 'col-5' },
      { key: 'status', sortable: false, thClass: 'col-2', tdClass: 'col-2' },
      { key: 'ReceivedOn', label: 'Received on', sortable: false, thClass: 'col-2', tdClass: 'col-2' },
      { key: 'Encrypted', label: 'E2E encryption', thClass: 'col-2', tdClass: 'col-2' }
    ]

    return {
      TABLE_FIELDS, formatDate
    }
  }
})
</script>
