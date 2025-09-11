import type { CodegenConfig } from '@graphql-codegen/cli';
import 'dotenv/config';

const config: CodegenConfig = {
  overwrite: true,
  schema: `${process.env.VITE_USER_SERVICE_URL || 'http://localhost:8080'}/graphql`,
  documents: "src/api/**/*.ts",
  generates: {
    "src/gql/": {
      preset: "client",
      plugins: []
    }
  }
};

export default config;