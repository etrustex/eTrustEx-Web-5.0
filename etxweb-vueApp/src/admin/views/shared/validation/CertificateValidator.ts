import { formatBytes } from '@/utils/formatters'
import { helpers, maxValue, required } from '@vuelidate/validators'
import { Identity } from '@/utils/crypto/pkcsReader'

const CERTIFICATE_MAX_FILE_SIZE = 102400

function isNotSelected(value: string) {
  return value !== 'No file chosen'
}

function selectedIdentify(value: boolean) {
  return value
}

function validateIdentity(value: Identity, params: any) {
  if (!value) {
    return true
  }
  const now = new Date()
  return value.notBefore < now && (params.allowExpired.value || value.notAfter > now)
}

const requiredCertificateFile = helpers.withMessage('It is required to select a certificate file', isNotSelected)
const requiredCertificatePassword = helpers.withMessage('It is required to input a password for the certificate', required)
const requiredCertificateIdentity = helpers.withMessage('It is required to select an identity', selectedIdentify)

const fileMaxSize = helpers.withMessage(`The certificate file cannot exceed ${formatBytes(CERTIFICATE_MAX_FILE_SIZE)}.`, maxValue(CERTIFICATE_MAX_FILE_SIZE + 1))

const validIdentity = helpers.withMessage('It is required to select a valid identity', validateIdentity)

const requiredPublicKey = helpers.withMessage('The public key is required.', required)
const requiredBusiness = helpers.withMessage('The business is required.', required)
const requiredEntity = helpers.withMessage('The entity is required.', required)

export const certificateRules = {
  p12FileName: {
    required: requiredCertificateFile
  },
  fileSize: {
    maxSize: fileMaxSize
  },
  p12Password: {
    requiredPassword: requiredCertificatePassword
  },
  isIdentitySelected: {
    requiredIdentity: requiredCertificateIdentity
  },
  selectedIdentity: {
    identity: validIdentity
  },
  allowExpired: {
    allowExpired: true
  }
}

export const compromisedCertificateRules = {
  publicKey: {
    required: requiredPublicKey
  },
  businessId: {
    required: requiredBusiness
  },
  entityId: {
    required: requiredEntity
  }
}

export const updateCertificateRules = {
  businessId: {
    required: requiredBusiness
  },
  entityId: {
    required: requiredEntity
  },
  euLoginId: {
    required: helpers.withMessage('The EU Login ID is required', required)
  }
}
