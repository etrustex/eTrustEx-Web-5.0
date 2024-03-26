import { ExchangeMode } from '@/model/entities'

export default class ExchangeRuleDto {
  channelId: number
  memberId: number
  exchangeMode: ExchangeMode
}
