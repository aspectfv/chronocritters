import { useState } from 'react';
import { Form, useActionData, useNavigation, Link } from 'react-router-dom';
import type { LoginCredentials, AuthError } from '@store/auth/types';

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
      <h1 className="text-2xl font-bold text-accent-ink text-center mb-8">
        Login to Chrono-Critters
      </h1>
      
      {actionData?.message && (
        <div className="mb-4 p-3 bg-danger-soft border border-danger/50 text-danger-ink rounded">
          {actionData.message}
        </div>
      )}
      
      <Form method="post" action="/auth/login" className="space-y-6">
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-accent-ink mb-2">
            Username
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-ink-faint" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
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
              className={`block w-full pl-10 pr-3 py-3 border rounded-lg bg-surface text-ink placeholder-ink-faint focus:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus:border-accent ${
                actionData?.field === 'username' ? 'border-danger/50' : 'border-line'
              }`}
              required
            />
          </div>
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-accent-ink mb-2">
            Password
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-ink-faint" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
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
              className={`block w-full pl-10 pr-3 py-3 border rounded-lg bg-surface text-ink placeholder-ink-faint focus:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus:border-accent ${
                actionData?.field === 'password' ? 'border-danger/50' : 'border-line'
              }`}
              required
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full bg-accent hover:bg-accent disabled:bg-accent-strong disabled:opacity-60 text-white font-semibold py-3 px-4 rounded-lg transition-colors focus:outline-none focus:ring-2 focus-visible:ring-accent-ring focus:ring-offset-2"
        >
          {isSubmitting ? 'Logging in...' : 'Login'}
        </button>
      </Form>

      <p className="mt-6 text-center text-sm text-ink-muted">
        Don't have an account?{' '}
        <Link 
          to="/auth/register"
          className="text-accent hover:text-accent-ink font-medium"
        >
          Register here
        </Link>
      </p>
    </>
  );
}

export default LoginForm;
