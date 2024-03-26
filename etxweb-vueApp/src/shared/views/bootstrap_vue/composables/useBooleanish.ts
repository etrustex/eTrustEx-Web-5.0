// function useBooleanish<T>(el: Ref<Booleanish | T>): ComputedRef<boolean | T>
// This may possibily be used in Vue 3.3 to include Booleanish and complex types ie Booleanish | string
import { Ref } from 'vue'
import Booleanish from '@/shared/views/bootstrap_vue/types/Booleanish'
import { resolveBooleanish } from '@/shared/views/bootstrap_vue/utils/booleanish'
import { computedEager } from '@vueuse/core'

/**
 * Resolves a Booleanish type reactively to type boolean
 */
function useBooleanish(el: Ref<Booleanish>): Readonly<Ref<boolean>>
function useBooleanish(el: Ref<Booleanish | null>): Readonly<Ref<boolean | null>>
function useBooleanish(el: Ref<Booleanish | undefined>): Readonly<Ref<boolean | undefined>>
function useBooleanish(
  el: Ref<Booleanish | undefined | null>
): Readonly<Ref<boolean | undefined | null>>
function useBooleanish(
  el:
    | Ref<Booleanish>
    | Ref<Booleanish | undefined>
    | Ref<Booleanish | null>
    | Ref<Booleanish | undefined | null>
):
  | Readonly<Ref<boolean>>
  | Readonly<Ref<boolean | undefined>>
  | Readonly<Ref<boolean | null>>
  | Readonly<Ref<boolean | undefined | null>> {
  return computedEager(() =>
    el.value === undefined || el.value === null ? el.value : resolveBooleanish(el.value)
  )
}

export default useBooleanish
