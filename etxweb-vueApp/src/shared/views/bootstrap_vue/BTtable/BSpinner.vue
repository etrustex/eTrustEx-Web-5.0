<template>
  <component
    :is="tag"
    :class="computedClasses"
    :role="label || hasLabelSlot ? role : null"
    :aria-hidden="label || hasLabelSlot ? null : true"
  >
    <span v-if="label || hasLabelSlot" class="visually-hidden">
      <slot name="label">{{ label }}</slot>
    </span>
  </component>
</template>

<script setup lang="ts">
import { computed, toRef, useSlots } from 'vue'
import Booleanish from '@/shared/views/bootstrap_vue/types/Booleanish'
import SpinnerType from '@/shared/views/bootstrap_vue/types/SpinnerType'
import ColorVariant from '@/shared/views/bootstrap_vue/types/ColorVariant'
import useBooleanish from '@/shared/views/bootstrap_vue/composables/useBooleanish'
import { isEmptySlot } from '@/shared/views/bootstrap_vue/utils/dom'

interface BSpinnerProps {
  label?: string
  role?: string
  small?: Booleanish
  tag?: string
  type?: SpinnerType
  variant?: ColorVariant
}

const props = withDefaults(defineProps<BSpinnerProps>(), {
  role: 'status',
  small: false,
  tag: 'span',
  type: 'border'
})

const slots = useSlots()

const smallBoolean = useBooleanish(toRef(props, 'small'))

const computedClasses = computed(() => ({
  'spinner-border': props.type === 'border',
  'spinner-border-sm': props.type === 'border' && smallBoolean.value,
  'spinner-grow': props.type === 'grow',
  'spinner-grow-sm': props.type === 'grow' && smallBoolean.value,
  [`text-${props.variant}`]: props.variant !== undefined
}))

const hasLabelSlot = computed<boolean>(() => !isEmptySlot(slots.label))
</script>
