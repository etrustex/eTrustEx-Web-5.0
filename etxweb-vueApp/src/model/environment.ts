export enum Environment {
  DEV = 'dev',
  TEST = 'test',
  ACC = 'acc',
  PROD = 'prod',
  ACC_BHS = 'acc_bhs'
}

export function getEnvironment(env: string): Environment {
  const parsed = env.toLowerCase()
    .replace(/-/gm, '_')

  const environment: Environment | undefined = Object.values(Environment)
    .find(x => x == parsed)

  if (environment === undefined) {
    throw Error('Environment not found!!!1')
  }

  return environment
}
