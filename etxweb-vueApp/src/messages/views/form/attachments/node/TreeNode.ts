import { AttachmentSpecWrapper } from '@/model/attachmentDto'
import { AttachmentSpec } from '@/model/entities'

export interface TreeNode {
  id: number,
  name: string,
  children: Array<TreeNode>
  attachmentSpecWrapper?: AttachmentSpecWrapper
  attachmentSpec?: AttachmentSpec
}
