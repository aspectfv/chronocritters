function AuthPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h1 className="text-center text-3xl font-bold text-gray-900">
            ChronoCritters
          </h1>
          <p className="mt-2 text-center text-sm text-gray-600">
            Sign in to start your adventure
          </p>
        </div>
        
        <div className="mt-8 space-y-6">
          <div className="space-y-4">
            <button className="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 px-4 rounded-lg font-semibold transition-colors">
              Login
            </button>
            <button className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 px-4 rounded-lg font-semibold transition-colors">
              Register
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;
