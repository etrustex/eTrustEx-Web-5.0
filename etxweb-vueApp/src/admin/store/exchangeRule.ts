import { ExchangeRule, RestResponsePage } from '@/model/entities'
import { PaginationOptions } from '@/utils/pagination/paginationOptions'
import { defineStore } from 'pinia'
import { ExchangeRuleApi } from '@/shared/api/exchangeRulesApi'
import ExchangeRuleDto from '@/model/ExchangeRuleDto'

const nameSpace = 'exchangeRule'

interface State {
  formVisible: boolean
  exchangeRules: Array<ExchangeRule>
  exchangeRulePage: RestResponsePage<ExchangeRule>
  exchangeRuleForm: ExchangeRuleDto

}

const useExchangeRuleStore = defineStore(nameSpace, {
  state: (): State => ({
    formVisible: false,
    exchangeRules: [],
    exchangeRulePage: new RestResponsePage<ExchangeRule>(),
    exchangeRuleForm: new ExchangeRuleDto()

  }),
  actions: {
    fetchExchangeRules(paginationOptions: PaginationOptions) {
      return ExchangeRuleApi.getExchangeRules(paginationOptions)
        .then((exchangeRulesPage) => {
          this.exchangeRules = exchangeRulesPage.content
          this.exchangeRulePage = exchangeRulesPage
        })
    },

    async setFormVisible(visible: boolean) {
      this.formVisible = visible
    },

    setExchangeRuleForm(exchangeRuleForm: ExchangeRuleDto) {
      this.exchangeRuleForm = exchangeRuleForm
    }
  }

})

export default useExchangeRuleStore
