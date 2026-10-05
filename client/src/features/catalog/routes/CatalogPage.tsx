import { Link, useLoaderData } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import type { GetCritterCatalogQuery } from '@/gql/graphql';
import { CatalogEntry } from '@features/catalog/components/CatalogEntry';
import { TypeWheel } from '@features/catalog/components/TypeWheel';

function CatalogPage() {
  const { critters, typeAdvantages } = useLoaderData() as GetCritterCatalogQuery;

  // Meters are scaled against the strongest critter in the catalogue rather
  // than a fixed ceiling, so they keep comparing as the roster is balanced.
  const ceilings = critters.reduce(
    (highest, critter) => ({
      health: Math.max(highest.health, critter.baseStats?.health ?? 0),
      attack: Math.max(highest.attack, critter.baseStats?.attack ?? 0),
      defense: Math.max(highest.defense, critter.baseStats?.defense ?? 0),
    }),
    { health: 0, attack: 0, defense: 0 },
  );

  const catalogTypes = [...new Set(critters.map((critter) => critter.type).filter((type) => type !== null && type !== undefined))];

  return (
    <main className="min-h-screen bg-arena text-arena-ink">
      <div className="mx-auto flex min-h-screen max-w-5xl flex-col gap-3 p-3 sm:p-5">
        <div className="panel flex items-center gap-2 rounded-lg bg-arena-deep px-2 py-1.5">
          <Link
            to="/menu"
            className="flex items-center gap-1.5 rounded-control px-2 py-1.5 text-sm font-semibold text-arena-ink-muted transition-colors hover:bg-arena-glass hover:text-arena-ink focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brass"
          >
            <ArrowLeft className="h-4 w-4" aria-hidden="true" />
            <span className="hidden sm:inline">Menu</span>
          </Link>

          <div className="flex flex-1 justify-center">
            <span className="display text-lg text-arena-ink">Critter catalogue</span>
          </div>

          <span className="numeral shrink-0 whitespace-nowrap px-2 text-sm text-arena-ink-muted">
            {critters.length} in play
          </span>
        </div>

        {critters.length === 0 ? (
          <p className="panel rounded-lg bg-arena-deep p-6 text-center text-sm text-arena-ink-muted">
            No critters are seeded yet. Start the user service and the catalogue fills itself.
          </p>
        ) : (
          <div className="grid grid-cols-1 gap-3 lg:grid-cols-[20rem_1fr]">
            <div className="lg:sticky lg:top-5 lg:self-start">
              <TypeWheel advantages={typeAdvantages} types={catalogTypes} />
            </div>

            <div className="flex flex-col gap-3">
              {critters.map((critter) => (
                <CatalogEntry key={critter.id} critter={critter} ceilings={ceilings} />
              ))}
            </div>
          </div>
        )}
      </div>
    </main>
  );
}

export default CatalogPage;
