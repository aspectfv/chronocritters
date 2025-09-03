import { useState } from 'react';
import { Form, useActionData, useNavigation, Link } from 'react-router-dom';
import type { LoginCredentials, AuthError } from '@features/auth/types';

function LoginForm() {
  const [formData, setFormData] = useState<LoginCredentials>({
    username: '',
    password: ''
  });
  
  const actionData = useActionData() as AuthError | undefined;
  const navigation = useNavigation();
  const isSubmitting = navigation.state === 'submitting';

  const handleInputChange = (field: keyof LoginCredentials) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  return (
    <>
      <h1 className="text-2xl font-bold text-green-700 text-center mb-8">
        Login to Chrono-Critters
      </h1>
      
      {actionData?.message && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {actionData.message}
        </div>
      )}
      
      <Form method="post" action="/auth/login" className="space-y-6">
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-green-700 mb-2">
            Username
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <input
              id="username"
              name="username"
              type="text"
              value={formData.username}
              onChange={handleInputChange('username')}
              placeholder="Enter your username"
              className={`block w-full pl-10 pr-3 py-3 border rounded-lg bg-white text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-green-300 focus:border-green-300 ${
                actionData?.field === 'username' ? 'border-red-400' : 'border-gray-200'
              }`}
              required
            />
          </div>
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-green-700 mb-2">
            Password
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleInputChange('password')}
              placeholder="Enter your password"
              className={`block w-full pl-10 pr-3 py-3 border rounded-lg bg-white text-gray-700 placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-green-300 focus:border-green-300 ${
                actionData?.field === 'password' ? 'border-red-400' : 'border-gray-200'
              }`}
              required
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full bg-green-600 hover:bg-green-700 disabled:bg-green-400 text-white font-semibold py-3 px-4 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
        >
          {isSubmitting ? 'Logging in...' : 'Login'}
        </button>
      </Form>

      <p className="mt-6 text-center text-sm text-gray-600">
        Don't have an account?{' '}
        <Link 
          to="/auth/register"
          className="text-green-600 hover:text-green-700 font-medium"
        >
          Register here
        </Link>
      </p>
    </>
  );
}

export default LoginForm;
