export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
};

export type Ability = {
  __typename?: 'Ability';
  description?: Maybe<Scalars['String']['output']>;
  effects?: Maybe<Array<Maybe<EffectUnion>>>;
  id?: Maybe<Scalars['ID']['output']>;
  name?: Maybe<Scalars['String']['output']>;
};

export type BaseStats = {
  __typename?: 'BaseStats';
  attack?: Maybe<Scalars['Int']['output']>;
  defense?: Maybe<Scalars['Int']['output']>;
  expToNextLevel?: Maybe<Scalars['Int']['output']>;
  experience?: Maybe<Scalars['Int']['output']>;
  health?: Maybe<Scalars['Int']['output']>;
  level?: Maybe<Scalars['Int']['output']>;
};

export type Critter = {
  __typename?: 'Critter';
  abilities?: Maybe<Array<Maybe<Ability>>>;
  baseStats?: Maybe<BaseStats>;
  description?: Maybe<Scalars['String']['output']>;
  id?: Maybe<Scalars['ID']['output']>;
  name?: Maybe<Scalars['String']['output']>;
  type?: Maybe<CritterType>;
};

export enum CritterType {
  Electric = 'ELECTRIC',
  Fire = 'FIRE',
  Grass = 'GRASS',
  Kinetic = 'KINETIC',
  Metal = 'METAL',
  Toxic = 'TOXIC',
  Unknown = 'UNKNOWN',
  Water = 'WATER'
}

export type DamageDealtEntry = {
  __typename?: 'DamageDealtEntry';
  damage: Scalars['Int']['output'];
  playerId: Scalars['ID']['output'];
};

export type DamageEffect = Effect & {
  __typename?: 'DamageEffect';
  damage: Scalars['Int']['output'];
  description: Scalars['String']['output'];
  id: Scalars['ID']['output'];
};

export type DamageOverTimeEffect = Effect & {
  __typename?: 'DamageOverTimeEffect';
  damagePerTurn: Scalars['Int']['output'];
  description: Scalars['String']['output'];
  duration: Scalars['Int']['output'];
  id: Scalars['ID']['output'];
};

export type Effect = {
  description: Scalars['String']['output'];
  id: Scalars['ID']['output'];
};

export type EffectUnion = DamageEffect | DamageOverTimeEffect | SkipTurnEffect;

export type LoginResponse = {
  __typename?: 'LoginResponse';
  token: Scalars['String']['output'];
  user: User;
};

export type MatchHistoryEntry = {
  __typename?: 'MatchHistoryEntry';
  battleId?: Maybe<Scalars['String']['output']>;
  damageDealt?: Maybe<Scalars['Int']['output']>;
  damageReceived?: Maybe<Scalars['Int']['output']>;
  duration?: Maybe<Scalars['Int']['output']>;
  loserId?: Maybe<Scalars['String']['output']>;
  opponentCrittersNames?: Maybe<Array<Maybe<Scalars['String']['output']>>>;
  opponentUsername?: Maybe<Scalars['String']['output']>;
  timestamp?: Maybe<Scalars['String']['output']>;
  turnActionHistory?: Maybe<Array<Maybe<TurnActionEntry>>>;
  turnCount?: Maybe<Scalars['Int']['output']>;
  usedCrittersNames?: Maybe<Array<Maybe<Scalars['String']['output']>>>;
  winnerId?: Maybe<Scalars['String']['output']>;
};

export type Mutation = {
  __typename?: 'Mutation';
  login?: Maybe<LoginResponse>;
  register?: Maybe<LoginResponse>;
};


export type MutationLoginArgs = {
  password: Scalars['String']['input'];
  username: Scalars['String']['input'];
};


export type MutationRegisterArgs = {
  password: Scalars['String']['input'];
  username: Scalars['String']['input'];
};

export type Player = {
  __typename?: 'Player';
  id?: Maybe<Scalars['ID']['output']>;
  matchHistory?: Maybe<Array<Maybe<MatchHistoryEntry>>>;
  password?: Maybe<Scalars['String']['output']>;
  roster?: Maybe<Array<Maybe<Critter>>>;
  stats?: Maybe<PlayerStats>;
  username?: Maybe<Scalars['String']['output']>;
};

export type PlayerStats = {
  __typename?: 'PlayerStats';
  expToNextLevel?: Maybe<Scalars['Int']['output']>;
  experience?: Maybe<Scalars['Int']['output']>;
  level?: Maybe<Scalars['Int']['output']>;
  losses?: Maybe<Scalars['Int']['output']>;
  wins?: Maybe<Scalars['Int']['output']>;
};

export type Query = {
  __typename?: 'Query';
  getMatchHistoryEntry?: Maybe<MatchHistoryEntry>;
  getPlayer?: Maybe<Player>;
};


export type QueryGetMatchHistoryEntryArgs = {
  battleId: Scalars['String']['input'];
  playerId: Scalars['String']['input'];
};


export type QueryGetPlayerArgs = {
  id: Scalars['ID']['input'];
};

export type SkipTurnEffect = Effect & {
  __typename?: 'SkipTurnEffect';
  description: Scalars['String']['output'];
  duration: Scalars['Int']['output'];
  id: Scalars['ID']['output'];
};

export type TurnActionEntry = {
  __typename?: 'TurnActionEntry';
  playerHasTurn?: Maybe<Scalars['Boolean']['output']>;
  playerId?: Maybe<Scalars['ID']['output']>;
  turn?: Maybe<Scalars['Int']['output']>;
  turnActionLog?: Maybe<Scalars['String']['output']>;
};

export type User = {
  __typename?: 'User';
  id: Scalars['ID']['output'];
  username: Scalars['String']['output'];
};

export type LoginMutationVariables = Exact<{
  username: Scalars['String']['input'];
  password: Scalars['String']['input'];
}>;


export type LoginMutation = { __typename?: 'Mutation', login?: { __typename?: 'LoginResponse', token: string, user: { __typename?: 'User', id: string, username: string } } | null };

export type RegisterMutationVariables = Exact<{
  username: Scalars['String']['input'];
  password: Scalars['String']['input'];
}>;


export type RegisterMutation = { __typename?: 'Mutation', register?: { __typename?: 'LoginResponse', token: string, user: { __typename?: 'User', id: string, username: string } } | null };

export type GetPlayerStatsQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetPlayerStatsQuery = { __typename?: 'Query', getPlayer?: { __typename?: 'Player', stats?: { __typename?: 'PlayerStats', wins?: number | null, losses?: number | null } | null } | null };

export type GetPlayerOverviewQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetPlayerOverviewQuery = { __typename?: 'Query', getPlayer?: { __typename?: 'Player', id?: string | null, username?: string | null, stats?: { __typename?: 'PlayerStats', wins?: number | null, losses?: number | null, level?: number | null, experience?: number | null, expToNextLevel?: number | null } | null, roster?: Array<{ __typename?: 'Critter', name?: string | null, description?: string | null, type?: CritterType | null, baseStats?: { __typename?: 'BaseStats', level?: number | null } | null } | null> | null } | null };

export type GetMyCrittersQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetMyCrittersQuery = { __typename?: 'Query', getPlayer?: { __typename?: 'Player', roster?: Array<{ __typename?: 'Critter', id?: string | null, name?: string | null, description?: string | null, type?: CritterType | null, baseStats?: { __typename?: 'BaseStats', level?: number | null, experience?: number | null, expToNextLevel?: number | null, health?: number | null, attack?: number | null, defense?: number | null } | null, abilities?: Array<{ __typename?: 'Ability', id?: string | null, name?: string | null, description?: string | null, effects?: Array<
          | { __typename?: 'DamageEffect', id: string, description: string, damage: number }
          | { __typename?: 'DamageOverTimeEffect', id: string, description: string, damagePerTurn: number, duration: number }
          | { __typename?: 'SkipTurnEffect', id: string, description: string, duration: number }
         | null> | null } | null> | null } | null> | null } | null };

export type GetPlayerResultsQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetPlayerResultsQuery = { __typename?: 'Query', getPlayer?: { __typename?: 'Player', username?: string | null, stats?: { __typename?: 'PlayerStats', level?: number | null, experience?: number | null, expToNextLevel?: number | null } | null, roster?: Array<{ __typename?: 'Critter', id?: string | null, name?: string | null, baseStats?: { __typename?: 'BaseStats', level?: number | null, experience?: number | null, expToNextLevel?: number | null } | null } | null> | null } | null };

export type GetBattleHistoryQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type GetBattleHistoryQuery = { __typename?: 'Query', getPlayer?: { __typename?: 'Player', matchHistory?: Array<{ __typename?: 'MatchHistoryEntry', battleId?: string | null, winnerId?: string | null, loserId?: string | null, opponentUsername?: string | null, timestamp?: string | null, usedCrittersNames?: Array<string | null> | null, opponentCrittersNames?: Array<string | null> | null, turnCount?: number | null, duration?: number | null, damageDealt?: number | null, damageReceived?: number | null, turnActionHistory?: Array<{ __typename?: 'TurnActionEntry', playerId?: string | null, playerHasTurn?: boolean | null, turn?: number | null, turnActionLog?: string | null } | null> | null } | null> | null } | null };

export type GetBattleHistoryEntryQueryVariables = Exact<{
  playerId: Scalars['String']['input'];
  battleId: Scalars['String']['input'];
}>;


export type GetBattleHistoryEntryQuery = { __typename?: 'Query', getMatchHistoryEntry?: { __typename?: 'MatchHistoryEntry', battleId?: string | null, winnerId?: string | null, loserId?: string | null, opponentUsername?: string | null, timestamp?: string | null, usedCrittersNames?: Array<string | null> | null, opponentCrittersNames?: Array<string | null> | null, turnCount?: number | null, duration?: number | null, damageDealt?: number | null, damageReceived?: number | null, turnActionHistory?: Array<{ __typename?: 'TurnActionEntry', playerId?: string | null, playerHasTurn?: boolean | null, turn?: number | null, turnActionLog?: string | null } | null> | null } | null };
