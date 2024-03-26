<template>
  <div class="container my-5">
    <div class="row">
      <div class="d-flex justify-content-between mt-3">
        <h2 class="h3 variant">User guides</h2>
        <div class="d-flex align-items-md-end">
          <a @click="editMode = true"
             class="ico-standalone"
             type="button"
             v-tooltip.tooltip="'Edit'">
            <i class="ico-edit"></i>
          </a>
        </div>
      </div>
    </div>

    <form class="form-admin" v-bind:class="(editMode)?'form-admin-edit-mode':'form-admin-view-mode'">
      <div class="row">
        <label for="userGuideInputUpdate" class="col-lg-4 col-form-label">
          User guide (PDF)<span v-if="isSystemComponent" class="label-required">*</span>
        </label>
        <div class="col-lg-8 d-flex align-items-center justify-content-between">
          <div v-if="editMode">
            <file-upload input-id="userGuideInputUpdate" class="btn btn-outline-primary btn-file-upload"
                         @input-file="userGuideInputUpdate">
                  <span class="ico-conjunction">
                    <i aria-hidden="true" class="ico-add-primary"></i>
                    <span>Select a file</span>
                    <i aria-hidden="true" class="ico-file-primary"></i>
                  </span>
            </file-upload>
            <input v-model="userGuide.filename" hidden type="text" @input="validate">
            <span v-if="v$.userGuide.filename.$error" class="error"> {{
                v$.userGuide.filename.$errors[0].$message
              }}
            </span>
          </div>
          <strong v-if="editMode">{{ userGuide.filename }}</strong>
          <strong v-else-if="userGuide.filename !== noFileChosen">{{ userGuide.filename }}</strong>
          <a v-if="userGuide.filename !== noFileChosen && editMode" href="javascript:void(0);"
             class="ico-standalone delete"
             type="button"
             v-tooltip.tooltip="'Delete'"
             @click.prevent="removeUserGuides()"
             @keydown.enter.prevent="onEnter">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </div>
      </div>

      <div class="row">
        <label for="userGuideInputUpdate" class="col-lg-4 col-form-label">
          Entity Admin guide (PDF)<span v-if="isSystemComponent" class="label-required">*</span>
        </label>
        <div class="col-lg-8 d-flex align-items-center justify-content-between">
          <div v-if="editMode">
            <file-upload input-id="entityAdminGuideInputUpdate" class="btn btn-outline-primary btn-file-upload"
                         @input-file="entityAdminGuideInputUpdate">
                <span class="ico-conjunction">
                  <i aria-hidden="true" class="ico-add-primary"></i>
                  <span>Select a file</span>
                  <i aria-hidden="true" class="ico-file-primary"></i>
                </span>
            </file-upload>
            <input v-model="entityAdminGuide.filename" hidden type="text" @input="validate">
            <span v-if="v$.entityAdminGuide.filename.$error"
                  class="error"> {{ v$.entityAdminGuide.filename.$errors[0].$message }} </span>
          </div>
          <strong v-if="editMode">{{ entityAdminGuide.filename }}</strong>
          <strong v-else-if="entityAdminGuide.filename !== noFileChosen">{{ entityAdminGuide.filename }}</strong>
          <a v-if="entityAdminGuide.filename !== noFileChosen && editMode"
             href="javascript:void(0);"
             class="ico-standalone delete"
             type="button"
             v-tooltip.tooltip="'Delete'"
             @click.prevent="removeEntityAdminGuide()"
             @keydown.enter.prevent="onEnter">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </div>
      </div>

      <div class="row">
        <label for="businessAdminGuideInputUpdate" class="col-lg-4 col-form-label">
          Business Admin guide (PDF)<span v-if="isSystemComponent" class="label-required">*</span>
        </label>
        <div class="col-lg-8 d-flex align-items-center justify-content-between">
          <div v-if="editMode">
            <file-upload input-id="businessAdminGuideInputUpdate" class="btn btn-outline-primary btn-file-upload"
                         @input-file="businessAdminGuideInputUpdate">
                <span class="ico-conjunction">
                  <i aria-hidden="true" class="ico-add-primary"></i>
                  <span>Select a file</span>
                  <i aria-hidden="true" class="ico-file-primary"></i>
                </span>
            </file-upload>
            <input v-model="businessAdminGuide.filename" hidden type="text" @input="validate">
            <span v-if="v$.businessAdminGuide.filename.$error"
                  class="error"> {{ v$.businessAdminGuide.filename.$errors[0].$message }} </span>
          </div>
          <strong v-if="editMode">{{ businessAdminGuide.filename }}</strong>
          <strong v-else-if="businessAdminGuide.filename !== noFileChosen">{{ businessAdminGuide.filename }}</strong>
          <a v-if="businessAdminGuide.filename !== noFileChosen && editMode"
             href="javascript:void(0);"
             class="ico-standalone delete"
             type="button"
             v-tooltip.tooltip="'Delete'"
             @click.prevent="removeBusinessAdminGuide()"
             @keydown.enter.prevent="onEnter">
            <i class="ico-delete"></i>
            <span>Remove</span>
          </a>
        </div>

      </div>

      <div v-if="editMode" class="row">
        <div class="col text-end btns">
          <button type="button" class="btn btn-outline-secondary" @click="cancel()">
            <span class="ico-conjunction"><i aria-hidden="true" class="ico-cancel"></i> Cancel </span>
          </button>
          <button type="button" class="btn btn-primary" @click="save">
            <span class="ico-conjunction"><i aria-hidden="true" class="ico-save-white"></i> Save </span>
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, PropType, ref, Ref } from 'vue'
import FileUpload from 'vue-upload-component'
import { Group, GroupType, RoleName, UserGuideSpec } from '@/model/entities'
import useDialogStore, { DialogType } from '@/shared/store/dialog'
import { UserGuideApi } from '@/shared/api/userGuideApi'
import { helpers } from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'

const props = defineProps({
  business: { type: Object as PropType<Group>, required: true }
})

const isSystemComponent: Ref<boolean> = ref(props.business?.type === GroupType.ROOT)
const isBusinessComponent: Ref<boolean> = ref(props.business?.type === GroupType.ROOT)

const dialogStore = useDialogStore()
const editMode: Ref<boolean> = ref(false)
const noFileChosen = 'No file chosen'

const userGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)
const entityAdminGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)
const businessAdminGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)

const savedUserGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)
const savedEntityAdminGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)
const savedBusinessAdminGuide: Ref<UserGuideSpec> = ref({ filename: noFileChosen } as UserGuideSpec)

let userGuideSpecs: Array<UserGuideSpec>

const validateUserGuideForSysAdmin = (value: string) => {
  return isSystemComponent.value && value.length !== 0 && value !== 'No file chosen'
}

const validateUserGuideForBusinessAdmin = () => {
  return true
}

const requiredGuide = {
  filename: {
    requiredUserGuide: helpers.withMessage('The user guide is required', isSystemComponent.value ? validateUserGuideForSysAdmin : validateUserGuideForBusinessAdmin)
  }
}

const userGuideRules = {
  userGuide: requiredGuide,
  entityAdminGuide: requiredGuide,
  businessAdminGuide: requiredGuide
}

const v$ = useVuelidate(userGuideRules, { userGuide, entityAdminGuide, businessAdminGuide })

const validate = () => {
  v$.value.$validate()
}

onBeforeMount(() => {
  getByBusiness()
})

const getByBusiness = () => {
  UserGuideApi.getByBusinessId(props.business.id)
    .then(userGuides => {
      userGuideSpecs = userGuides
      setUserGuides()
    })
}

const reset = () => {
  editMode.value = false

  userGuide.value = { ...savedUserGuide.value }
  entityAdminGuide.value = { ...savedEntityAdminGuide.value }
  businessAdminGuide.value = { ...savedBusinessAdminGuide.value }
  getByBusiness()
  v$.value.$reset()
}

const save = () => {
  v$.value.$validate()
  if (!v$.value.$error) {
    if (isPdf(userGuide.value.filename) && isPdf(entityAdminGuide.value.filename) && isPdf(businessAdminGuide.value.filename)) {
      editMode.value = false
      Promise.all([
        updateUserGuide(),
        updateEntityAdminGuide(),
        updateBusinessAdminGuide()
      ])
        .then(() => getByBusiness())
    } else {
      dialogStore.show('Cannot save the file!', '<p class="mt-3">The file could not be saved. Only PDF format is allowed.</p><p class="mt-3"> Please check the file format</p>')
    }
  }
}

const updateUserGuide = () => {
  if (userGuide.value.filename !== noFileChosen) {
    if (userGuide.value.file && userGuide.value.file.size) {
      return UserGuideApi.upload(userGuide.value.file, userGuide.value)
    }
  } else {
    if (isSystemComponent.value) {
      displayErrorMessage()
    } else {
      return UserGuideApi.delete(userGuide.value)
    }
  }
}

const updateEntityAdminGuide = () => {
  if (entityAdminGuide.value.filename !== noFileChosen) {
    if (entityAdminGuide.value.file && entityAdminGuide.value.file.size) {
      return UserGuideApi.upload(entityAdminGuide.value.file, entityAdminGuide.value)
    }
  } else {
    if (isBusinessComponent.value) {
      displayErrorMessage()
    } else {
      return UserGuideApi.delete(entityAdminGuide.value)
    }
  }
}

const updateBusinessAdminGuide = () => {
  if (businessAdminGuide.value.filename !== noFileChosen) {
    if (businessAdminGuide.value.file && businessAdminGuide.value.file.size) {
      return UserGuideApi.upload(businessAdminGuide.value.file, businessAdminGuide.value)
    }
  } else {
    if (isSystemComponent.value) {
      displayErrorMessage()
    } else {
      return UserGuideApi.delete(businessAdminGuide.value)
    }
  }
}

const displayErrorMessage = () => {
  dialogStore.show('Cannot delete the file!', '<p class="mt-3">You cannot delete the user guide for system admin.', DialogType.ERROR)
  setUserGuides()
}

const cancel = async () => {
  reset()
}

const isPdf = (fileName: string) => {
  if (fileName === noFileChosen) {
    return true
  }
  const extension = fileName.substring((fileName.lastIndexOf('.') + 1))
  return extension === 'pdf'
}

const userGuideInputUpdate = (target: any) => {
  userGuide.value = toUserGuideSpec(target.file)
}

const entityAdminGuideInputUpdate = (target: any) => {
  entityAdminGuide.value = toEntityAdminGuideSpec(target.file)
}

const businessAdminGuideInputUpdate = (target: any) => {
  businessAdminGuide.value = toBusinessAdminGuideSpec(target.file)
}

const removeUserGuides = () => {
  userGuide.value.filename = noFileChosen
}

const removeEntityAdminGuide = () => {
  entityAdminGuide.value.filename = noFileChosen
}

const removeBusinessAdminGuide = () => {
  businessAdminGuide.value.filename = noFileChosen
}

const setUserGuides = () => {
  userGuideSpecs.forEach(userGuideSpec => {
    userGuideSpec = userGuideSpec.filename ? userGuideSpec : { filename: noFileChosen } as UserGuideSpec
    if (userGuideSpec.groupType === GroupType.ENTITY) {
      if (userGuideSpec.role === RoleName.OPERATOR) {
        userGuide.value = { ...userGuideSpec }
        savedUserGuide.value = { ...userGuideSpec }
      } else {
        entityAdminGuide.value = { ...userGuideSpec }
        savedEntityAdminGuide.value = { ...userGuideSpec }
      }
    } else {
      businessAdminGuide.value = { ...userGuideSpec }
      savedBusinessAdminGuide.value = { ...userGuideSpec }
    }
  })
}

const toUserGuideSpec = (file: File): UserGuideSpec => {
  return {
    filename: file.name,
    role: RoleName.OPERATOR,
    groupType: GroupType.ENTITY,
    businessId: props.business.id,
    file
  } as UserGuideSpec
}

const toEntityAdminGuideSpec = (file: File): UserGuideSpec => {
  return {
    filename: file.name,
    binary: file.arrayBuffer(),
    role: RoleName.GROUP_ADMIN,
    groupType: GroupType.ENTITY,
    businessId: props.business.id,
    file
  } as UserGuideSpec
}

const toBusinessAdminGuideSpec = (file: File): UserGuideSpec => {
  return {
    filename: file.name,
    binary: file.arrayBuffer(),
    role: RoleName.GROUP_ADMIN,
    groupType: GroupType.BUSINESS,
    businessId: props.business.id,
    file
  } as UserGuideSpec
}

</script>
