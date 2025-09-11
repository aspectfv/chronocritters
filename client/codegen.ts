// client/codegen.ts

import type { CodegenConfig } from '@graphql-codegen/cli';
import 'dotenv/config';

const config: CodegenConfig = {
  overwrite: true,
  schema: `${process.env.VITE_USER_SERVICE_URL || 'http://localhost:8080'}/graphql`,
  documents: ["src/api/**/*.ts", "src/api/fragments.ts"],
  generates: {
    "src/gql/graphql.ts": {
      plugins: [
        "typescript",
        "typescript-operations",
      ]
    }
  },
};

export default config;