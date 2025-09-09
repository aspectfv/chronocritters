import { Link, useParams } from 'react-router-dom';

const mockBattleDetails = {
    opponentName: 'StormCaller',
    result: 'Win',
    date: '2024-01-15',
    duration: '3:45',
    totalTurns: 8,
    yourCritterName: 'Flamewyrm',
    opponentCritterName: 'Thunderbeast',
    damageDealt: 245,
    damageReceived: 180,
    turnActions: [
        {
            turnNumber: 1,
            playerAction: { type: 'Attack' as const, description: 'Flamewyrm used Fire Blast - 45 damage' },
            opponentAction: { type: 'Attack' as const, description: 'Thunderbeast used Thunder Strike - 38 damage' },
        },
        {
            turnNumber: 2,
            playerAction: { type: 'Defend' as const, description: 'Flamewyrm used Flame Shield - Defense +20%' },
            opponentAction: { type: 'Attack' as const, description: 'Thunderbeast used Lightning Bolt - 25 damage' },
        },
        {
            turnNumber: 3,
            playerAction: { type: 'Attack' as const, description: 'Flamewyrm used Dragon Claw - 52 damage' },
            opponentAction: { type: 'Attack' as const, description: 'Thunderbeast used Static Shield - Defense +15%' },
        },
        {
            turnNumber: 4,
            playerAction: { type: 'Attack' as const, description: 'Flamewyrm used Fire Blast - 48 damage' },
            opponentAction: { type: 'Attack' as const, description: 'Thunderbeast used Thunder Strike - 42 damage' },
        },
    ],
};

const ActionTag = ({ type }: { type: 'Attack' | 'Defend' }) => {
    const styles = {
        Attack: 'bg-orange-400',
        Defend: 'bg-yellow-400',
    };
    return (
        <span className={`text-white text-xs font-semibold px-2 py-1 rounded ${styles[type]}`}>
            {type}
        </span>
    );
};

const ActionItem = ({ turn, actor, action }: { turn: number, actor: string, action: { type: 'Attack' | 'Defend', description: string } }) => (
    <div className="flex items-stretch gap-4">
        <div className="w-12 flex items-center justify-center">
            <span className="text-sm font-medium text-gray-500">Turn {turn}</span>
        </div>
        <div className="flex-1 bg-white p-3 rounded-lg border border-gray-200">
            <div className="flex items-center gap-2 mb-1">
                <p className="font-bold text-gray-800">{actor}</p>
                <ActionTag type={action.type} />
            </div>
            <p className="text-sm text-gray-600">{action.description}</p>
        </div>
    </div>
);


export function BattleHistoryDetails() {
    // const { battleId } = useParams<{ battleId: string }>();
    const battle = mockBattleDetails;

    const allActions = battle.turnActions.flatMap(t => [
        { turn: t.turnNumber, actor: 'You', action: t.playerAction },
        { turn: t.turnNumber, actor: battle.opponentName, action: t.opponentAction },
    ]);
    
    return (
        <div className="space-y-6">
            <div className="bg-white p-4 sm:p-6 rounded-lg shadow-sm border border-gray-200">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold text-green-800 flex items-center gap-2">
                        <span className="text-green-500">@</span> Battle Details: vs {battle.opponentName}
                    </h2>
                    <Link to="/profile/history" className="text-sm bg-gray-100 px-3 py-2 rounded-lg hover:bg-gray-200 font-semibold text-gray-700 flex items-center gap-1">
                        &larr; Back to History
                    </Link>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div className="space-y-4">
                        <div className="bg-green-500 text-white text-lg font-bold text-center py-2 px-4 rounded-lg">Win</div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Date</p>
                            <p className="font-medium text-gray-800">{battle.date}</p>
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Duration</p>
                            <p className="font-medium text-gray-800 flex items-center gap-1">
                                <svg className="h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                                {battle.duration}
                            </p>
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Total Turns</p>
                            <p className="font-medium text-gray-800">{battle.totalTurns}</p>
                        </div>
                    </div>
                    <div className="space-y-3">
                         <h3 className="text-md font-bold text-green-800">Critters</h3>
                         <div className="bg-green-50 p-3 rounded-lg border border-green-200">
                             <p className="text-sm font-semibold">Your Critter</p>
                             <p>{battle.yourCritterName}</p>
                         </div>
                         <div className="bg-gray-100 p-3 rounded-lg border border-gray-200">
                             <p className="text-sm font-semibold">Opponent's Critter</p>
                             <p>{battle.opponentCritterName}</p>
                         </div>
                    </div>
                    <div className="space-y-3">
                        <h3 className="text-md font-bold text-green-800">Damage Summary</h3>
                        <div className="bg-green-100 p-4 rounded-lg border border-green-200">
                            <p className="text-sm font-semibold text-green-900">Damage Dealt</p>
                            <p className="text-3xl font-bold text-green-700">{battle.damageDealt}</p>
                        </div>
                         <div className="bg-red-50 p-4 rounded-lg border border-red-200">
                            <p className="text-sm font-semibold text-red-900">Damage Received</p>
                            <p className="text-3xl font-bold text-red-700">{battle.damageReceived}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="bg-gray-50/50 p-4 sm:p-6 rounded-lg shadow-sm border border-gray-200">
                <h3 className="text-xl font-bold text-green-800 mb-4">Turn-by-Turn Actions</h3>
                <div className="space-y-3">
                    {allActions.map((item, index) => (
                        <ActionItem key={index} turn={item.turn} actor={item.actor} action={item.action} />
                    ))}
                </div>
            </div>
        </div>
    );
}