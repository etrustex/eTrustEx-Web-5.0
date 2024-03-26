import { Message, MessageSummary, MessageSummaryUserStatus } from '@/model/entities'

export function filterRead(userStatus: MessageSummaryUserStatus) {
  return userStatus.status === 'READ'
}

export function isMessageRead(message: Message) {
  return message.messageUserStatuses?.some(m => filterRead(m))
}

export function isMessageSummaryRead(messageSummary: MessageSummary) {
  return messageSummary.messageSummaryUserStatuses?.some(m => filterRead(m))
}
