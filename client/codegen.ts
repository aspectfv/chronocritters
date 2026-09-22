// client/codegen.ts

import type { CodegenConfig } from '@graphql-codegen/cli';

const config: CodegenConfig = {
  overwrite: true,
  // Read the schema from the user service's source rather than a running
  // instance, so codegen works offline and in CI.
  schema: '../user/src/main/resources/graphql/schema.graphqls',
  documents: ['src/api/**/*.ts'],
  generates: {
    'src/gql/graphql.ts': {
      plugins: [
        'typescript',
        'typescript-operations',
      ],
    },
  },
};

export default config;
