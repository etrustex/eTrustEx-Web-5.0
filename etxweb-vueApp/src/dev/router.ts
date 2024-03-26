import { RouteRecordRaw } from 'vue-router'
import OrphanFiles from '@/dev/views/OrphanFiles.vue'
import NewCertificateJob from '@/dev/views/NewCertificateJob.vue'
import IntegrationTest from '@/dev/views/IntegrationTest.vue'
import TestCleanUp from '@/dev/views/TestCleanUp.vue'
import UVScanTest from '@/dev/views/UVScanTest.vue'
import AccessToken from '@/dev/views/AccessToken.vue'

export const enum DevRouteNames {
  DELETE_ORPHAN_FILES = 'deleteOrphanFiles',
  NEW_CERTIFICATE_JOB = 'newCertificateJob',
  INTEGRATION_TEST_SUBMIT_DOCUMENT = 'integrationTestSubmitDocument',
  TEST_CLEAN_UP = 'testsCleanUp',
  UVSCAN_TEST = 'uvScanTest',
  ACCESS_TOKEN = 'accessToken'
}

export const devRoutes: Array<RouteRecordRaw> = [
  {
    path: '/orphan-files/delete',
    name: DevRouteNames.DELETE_ORPHAN_FILES,
    component: OrphanFiles
  },
  {
    path: '/new-certificate/launch-job',
    name: DevRouteNames.NEW_CERTIFICATE_JOB,
    component: NewCertificateJob
  },
  {
    path: '/integration-test/submit-document',
    name: DevRouteNames.INTEGRATION_TEST_SUBMIT_DOCUMENT,
    component: IntegrationTest
  },
  {
    path: '/clean-up/parent/:parentIdentifier/group/:groupIdentifier',
    name: DevRouteNames.TEST_CLEAN_UP,
    component: TestCleanUp
  },
  {
    path: '/uvscan/test',
    name: DevRouteNames.UVSCAN_TEST,
    component: UVScanTest
  },
  {
    path: '/test/access-token',
    name: DevRouteNames.ACCESS_TOKEN,
    component: AccessToken
  }
]
