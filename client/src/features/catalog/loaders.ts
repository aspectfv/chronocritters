import { getCritterCatalog } from '@api/user';

export async function catalogLoader() {
  return getCritterCatalog();
}
