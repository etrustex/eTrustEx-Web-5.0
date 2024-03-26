import { computed, type ComputedRef, type Ref } from 'vue'
import getId from '@/shared/views/bootstrap_vue/utils/getId'

/**
 * @param id
 * @param suffix
 * @returns
 */
export default (id?: Ref<string | undefined>, suffix?: string): ComputedRef<string> =>
  computed(() => id?.value || getId(suffix))
