/* eslint-disable */
import * as types from './graphql';
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';

/**
 * Map of all GraphQL operations in the project.
 *
 * This map has several performance disadvantages:
 * 1. It is not tree-shakeable, so it will include all operations in the project.
 * 2. It is not minifiable, so the string of a GraphQL query will be multiple times inside the bundle.
 * 3. It does not support dead code elimination, so it will add unused operations.
 *
 * Therefore it is highly recommended to use the babel or swc plugin for production.
 * Learn more about it here: https://the-guild.dev/graphql/codegen/plugins/presets/preset-client#reducing-bundle-size
 */
type Documents = {
    "\n  fragment EffectFields on Effect {\n    id\n    type\n    __typename\n    ... on DamageEffect {\n      damage\n    }\n    ... on DamageOverTimeEffect {\n      damagePerTurn\n      duration\n    }\n    ... on SkipTurnEffect {\n      duration\n    }\n  }\n": typeof types.EffectFieldsFragmentDoc,
    "\n  mutation Login($username: String!, $password: String!) {\n    login(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n": typeof types.LoginDocument,
    "\n  mutation Register($username: String!, $password: String!) {\n    register(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n": typeof types.RegisterDocument,
    "\n  query GetPlayerStats($id: ID!) {\n    getPlayer(id: $id) {\n      stats {\n        wins\n        losses\n      }\n    }\n  }\n": typeof types.GetPlayerStatsDocument,
    "\n  query GetPlayerOverview($id: ID!) {\n    getPlayer(id: $id) {\n      id\n      username\n      stats {\n        wins\n        losses\n      }\n      roster {\n        name\n        type\n      }\n    }\n  }\n": typeof types.GetPlayerOverviewDocument,
    "\n  #import \"./fragments.ts\"\n  query GetMyCritters($id: ID!) {\n    getPlayer(id: $id) {\n      roster {\n        id\n        name\n        type\n        baseStats {\n          health\n          attack\n          defense\n        }\n        abilities {\n          id\n          name\n          effects {\n            ...EffectFields\n          }\n        }\n      }\n    }\n  }\n": typeof types.GetMyCrittersDocument,
};
const documents: Documents = {
    "\n  fragment EffectFields on Effect {\n    id\n    type\n    __typename\n    ... on DamageEffect {\n      damage\n    }\n    ... on DamageOverTimeEffect {\n      damagePerTurn\n      duration\n    }\n    ... on SkipTurnEffect {\n      duration\n    }\n  }\n": types.EffectFieldsFragmentDoc,
    "\n  mutation Login($username: String!, $password: String!) {\n    login(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n": types.LoginDocument,
    "\n  mutation Register($username: String!, $password: String!) {\n    register(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n": types.RegisterDocument,
    "\n  query GetPlayerStats($id: ID!) {\n    getPlayer(id: $id) {\n      stats {\n        wins\n        losses\n      }\n    }\n  }\n": types.GetPlayerStatsDocument,
    "\n  query GetPlayerOverview($id: ID!) {\n    getPlayer(id: $id) {\n      id\n      username\n      stats {\n        wins\n        losses\n      }\n      roster {\n        name\n        type\n      }\n    }\n  }\n": types.GetPlayerOverviewDocument,
    "\n  #import \"./fragments.ts\"\n  query GetMyCritters($id: ID!) {\n    getPlayer(id: $id) {\n      roster {\n        id\n        name\n        type\n        baseStats {\n          health\n          attack\n          defense\n        }\n        abilities {\n          id\n          name\n          effects {\n            ...EffectFields\n          }\n        }\n      }\n    }\n  }\n": types.GetMyCrittersDocument,
};

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 *
 *
 * @example
 * ```ts
 * const query = graphql(`query GetUser($id: ID!) { user(id: $id) { name } }`);
 * ```
 *
 * The query argument is unknown!
 * Please regenerate the types.
 */
export function graphql(source: string): unknown;

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  fragment EffectFields on Effect {\n    id\n    type\n    __typename\n    ... on DamageEffect {\n      damage\n    }\n    ... on DamageOverTimeEffect {\n      damagePerTurn\n      duration\n    }\n    ... on SkipTurnEffect {\n      duration\n    }\n  }\n"): (typeof documents)["\n  fragment EffectFields on Effect {\n    id\n    type\n    __typename\n    ... on DamageEffect {\n      damage\n    }\n    ... on DamageOverTimeEffect {\n      damagePerTurn\n      duration\n    }\n    ... on SkipTurnEffect {\n      duration\n    }\n  }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  mutation Login($username: String!, $password: String!) {\n    login(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n"): (typeof documents)["\n  mutation Login($username: String!, $password: String!) {\n    login(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  mutation Register($username: String!, $password: String!) {\n    register(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n"): (typeof documents)["\n  mutation Register($username: String!, $password: String!) {\n    register(username: $username, password: $password) {\n      user {\n        id\n        username\n      }\n      token\n    }\n  }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  query GetPlayerStats($id: ID!) {\n    getPlayer(id: $id) {\n      stats {\n        wins\n        losses\n      }\n    }\n  }\n"): (typeof documents)["\n  query GetPlayerStats($id: ID!) {\n    getPlayer(id: $id) {\n      stats {\n        wins\n        losses\n      }\n    }\n  }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  query GetPlayerOverview($id: ID!) {\n    getPlayer(id: $id) {\n      id\n      username\n      stats {\n        wins\n        losses\n      }\n      roster {\n        name\n        type\n      }\n    }\n  }\n"): (typeof documents)["\n  query GetPlayerOverview($id: ID!) {\n    getPlayer(id: $id) {\n      id\n      username\n      stats {\n        wins\n        losses\n      }\n      roster {\n        name\n        type\n      }\n    }\n  }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n  #import \"./fragments.ts\"\n  query GetMyCritters($id: ID!) {\n    getPlayer(id: $id) {\n      roster {\n        id\n        name\n        type\n        baseStats {\n          health\n          attack\n          defense\n        }\n        abilities {\n          id\n          name\n          effects {\n            ...EffectFields\n          }\n        }\n      }\n    }\n  }\n"): (typeof documents)["\n  #import \"./fragments.ts\"\n  query GetMyCritters($id: ID!) {\n    getPlayer(id: $id) {\n      roster {\n        id\n        name\n        type\n        baseStats {\n          health\n          attack\n          defense\n        }\n        abilities {\n          id\n          name\n          effects {\n            ...EffectFields\n          }\n        }\n      }\n    }\n  }\n"];

export function graphql(source: string) {
  return (documents as any)[source] ?? {};
}

export type DocumentType<TDocumentNode extends DocumentNode<any, any>> = TDocumentNode extends DocumentNode<  infer TType,  any>  ? TType  : never;