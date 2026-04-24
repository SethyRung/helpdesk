/**
 * Resolves an asset path for Keycloak theme by prepending the resourcePath.
 *
 * In production Keycloak themes, assets are served from a dynamic path
 * (window.$kcData.resourcePath) that Keycloak provides at runtime.
 */
export function kcAsset(path: string): string {
  const resourcePath = (window as any).$kcData?.resourcePath as string | undefined;

  if (import.meta.env.PROD && resourcePath) {
    return path.replace("/assets/", resourcePath);
  }

  return path;
}
