import { LogLevel, PublicClientApplication } from '@azure/msal-browser'
import type { Configuration } from '@azure/msal-browser'

const clientId = import.meta.env.VITE_MSAL_CLIENT_ID as string | undefined
const tenantId = import.meta.env.VITE_MSAL_TENANT_ID as string | undefined

export const msalEnabled = Boolean(clientId && tenantId)

export const apiScope = import.meta.env.VITE_API_SCOPE
  ? (import.meta.env.VITE_API_SCOPE as string)
  : clientId
    ? `api://${import.meta.env.VITE_API_CLIENT_ID as string}/.default`
    : ''

export const msalConfig: Configuration = {
  auth: {
    clientId: clientId ?? '',
    authority: tenantId ? `https://login.microsoftonline.com/${tenantId}` : '',
    redirectUri: import.meta.env.VITE_REDIRECT_URI ?? window.location.origin,
    postLogoutRedirectUri: window.location.origin,
  },
  cache: {
    cacheLocation: 'localStorage',
  },
  system: {
    loggerOptions: {
      loggerCallback: (_level, message) => {
        if (import.meta.env.DEV) console.debug('[msal]', message)
      },
      logLevel: LogLevel.Warning,
    },
  },
}

export const msalInstance = new PublicClientApplication(msalConfig)

export async function obtenerAccessToken(): Promise<string | undefined> {
  if (!msalEnabled) return undefined

  const accounts = msalInstance.getAllAccounts()
  if (accounts.length === 0) {
    void msalInstance.loginRedirect({ scopes: apiScope ? [apiScope] : [] })
    throw new Error('Redirigiendo al inicio de sesión…')
  }

  const request = { scopes: apiScope ? [apiScope] : [], account: accounts[0] }
  try {
    const resultado = await msalInstance.acquireTokenSilent(request)
    return resultado.accessToken
  } catch {
    void msalInstance.acquireTokenRedirect(request)
    throw new Error('Se requiere reautenticación')
  }
}

export function nombreUsuario(): string | null {
  return msalInstance.getAllAccounts()[0]?.username ?? null
}
