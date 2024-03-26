import useCurrentGroupStore from '@/shared/store/currentGroup'
import { RouteRecordRaw } from 'vue-router'
import MessagesBox from '@/messages/views/MessagesBox.vue'
import MessagesList from '@/messages/views/lists/MessagesList.vue'
import { Status } from '@/model/entities'
import NoMessageSelected from '@/messages/views/NoMessageSelected.vue'
import MessageDetails from '@/messages/views/details/MessageDetails.vue'
import MessageForm from '@/messages/views/form/MessageForm.vue'

export const enum MessageBoxRouteNames {
  MESSAGES_BOX = '',
  SENT = 'sent',
  SENT_OVERVIEW = 'sentOverview',
  SENT_MSG_DETAILS = 'sentMessageDetails',
  SENT_DOWNLOAD_ATTACHMENT = 'sentDownloadAttachment',
  SENT_DOWNLOAD_ALL_ATTACHMENTS = 'sentDownloadAllAttachments',
  INBOX = 'inbox',
  INBOX_OVERVIEW = 'inboxOverview',
  INBOX_MSG_DETAILS = 'inboxMessageDetails',
  INBOX_DOWNLOAD_ATTACHMENT = 'inboxDownloadAttachment',
  INBOX_DOWNLOAD_ALL_ATTACHMENTS = 'inboxDownloadAllAttachments',
  DRAFT = 'draft',
  DRAFT_OVERVIEW = 'draftOverview',
  DRAFT_MSG_DETAILS = 'draftMessageDetails'
}

export function isOverviewRoute(route: MessageBoxRouteNames): boolean {
  return route === MessageBoxRouteNames.INBOX_OVERVIEW ||
    route === MessageBoxRouteNames.SENT_OVERVIEW ||
    route === MessageBoxRouteNames.DRAFT_OVERVIEW
}

export function isInboxRoute(route: MessageBoxRouteNames): boolean {
  return route === MessageBoxRouteNames.INBOX ||
    route === MessageBoxRouteNames.INBOX_OVERVIEW ||
    route === MessageBoxRouteNames.INBOX_MSG_DETAILS ||
    route === MessageBoxRouteNames.INBOX_DOWNLOAD_ATTACHMENT ||
    route === MessageBoxRouteNames.INBOX_DOWNLOAD_ALL_ATTACHMENTS
}

export function isSentRoute(route: MessageBoxRouteNames): boolean {
  return route === MessageBoxRouteNames.SENT ||
    route === MessageBoxRouteNames.SENT_OVERVIEW ||
    route === MessageBoxRouteNames.SENT_MSG_DETAILS ||
    route === MessageBoxRouteNames.SENT_DOWNLOAD_ATTACHMENT ||
    route === MessageBoxRouteNames.SENT_DOWNLOAD_ALL_ATTACHMENTS
}

export function isDraftRoute(route: MessageBoxRouteNames): boolean {
  return route === MessageBoxRouteNames.DRAFT ||
    route === MessageBoxRouteNames.DRAFT_OVERVIEW ||
    route === MessageBoxRouteNames.DRAFT_MSG_DETAILS
}

export function isSectionChanged(to: MessageBoxRouteNames, from: MessageBoxRouteNames): boolean {
  return !from || (isInboxRoute(to) && !isInboxRoute(from)) ||
    (isSentRoute(to) && !isSentRoute(from)) ||
    (isDraftRoute(to) && !isDraftRoute(from))
}

export const enum MessageFormRouteNames {
  MESSAGE_FORM = 'messageForm'
}

export const messageRoutes: Array<RouteRecordRaw> = [
  {
    path: 'mbox/:currentGroupId',
    name: MessageBoxRouteNames.MESSAGES_BOX,
    component: MessagesBox,
    redirect: { name: MessageBoxRouteNames.INBOX },
    children: [
      {
        path: `${MessageBoxRouteNames.INBOX}`,
        name: MessageBoxRouteNames.INBOX,
        component: MessagesList,
        props: {
          initFilters: {
            subject_or_sender: '',
            unread: false
          }
        },
        redirect: { name: MessageBoxRouteNames.INBOX_OVERVIEW },
        children: [
          {
            path: 'overview',
            name: MessageBoxRouteNames.INBOX_OVERVIEW,
            component: NoMessageSelected
          },
          {
            path: 'details/:messageId',
            name: MessageBoxRouteNames.INBOX_MSG_DETAILS,
            component: MessageDetails
          },
          {
            path: 'download-all/:messageId',
            name: MessageBoxRouteNames.INBOX_DOWNLOAD_ALL_ATTACHMENTS,
            component: MessageDetails
          },
          {
            path: 'download/:messageId/:attachmentId',
            name: MessageBoxRouteNames.INBOX_DOWNLOAD_ATTACHMENT,
            component: MessageDetails
          }
        ]
      },
      {
        path: `${MessageBoxRouteNames.SENT}`,
        name: MessageBoxRouteNames.SENT,
        component: MessagesList,
        props: {
          initFilters: {
            status: '',
            subject_or_recipient: '',
            unread: false
          }
        },
        redirect: { name: MessageBoxRouteNames.SENT_OVERVIEW },
        children: [
          {
            path: 'overview',
            name: MessageBoxRouteNames.SENT_OVERVIEW,
            component: NoMessageSelected

          },
          {
            path: 'details/:messageId',
            name: MessageBoxRouteNames.SENT_MSG_DETAILS,
            component: MessageDetails
          },
          {
            path: 'download-all/:messageId',
            name: MessageBoxRouteNames.SENT_DOWNLOAD_ALL_ATTACHMENTS,
            component: MessageDetails
          },
          {
            path: 'download/:messageId/:attachmentId',
            name: MessageBoxRouteNames.SENT_DOWNLOAD_ATTACHMENT,
            component: MessageDetails
          }
        ]
      },
      {
        path: `${MessageBoxRouteNames.DRAFT}`,
        name: MessageBoxRouteNames.DRAFT,
        component: MessagesList,
        props: {
          initFilters: {
            status: Status.DRAFT,
            subject_or_recipient: '',
            unread: false
          }
        },
        redirect: { name: MessageBoxRouteNames.DRAFT_OVERVIEW },
        children: [
          {
            path: 'overview',
            name: MessageBoxRouteNames.DRAFT_OVERVIEW,
            component: NoMessageSelected

          },
          {
            path: 'details/:messageId',
            name: MessageBoxRouteNames.DRAFT_MSG_DETAILS,
            component: MessageDetails
          }
        ]
      }
    ]
  },
  {
    path: 'message-form/:currentGroupId',
    name: MessageFormRouteNames.MESSAGE_FORM,
    component: MessageForm,
    beforeEnter: (to, from, next) => {
      const currentGroupStore = useCurrentGroupStore()
      if (currentGroupStore.recipients.length === 0) {
        return currentGroupStore.changeGroup(+to.params.currentGroupId).then(() => next())
      }
      next()
    }
  }
]
