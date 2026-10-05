import { useState } from 'react';
import { Form, useActionData, useNavigation, Link } from 'react-router-dom';
import { KeyRound, User } from 'lucide-react';
import type { LoginCredentials, AuthError } from '@store/auth/types';
import { AuthField } from '@components/auth/AuthField';

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
      <h1 className="display mb-4 text-xl text-arena-ink">Sign in</h1>

      {actionData?.message && (
        <p role="alert" className="mb-4 rounded-sm border-2 border-outline bg-ruby px-3 py-2 text-sm font-bold text-white">
          {actionData.message}
        </p>
      )}

      <Form method="post" action="/auth/login" className="space-y-4">
        <AuthField
          id="username"
          name="username"
          type="text"
          label="Username"
          placeholder="Your trainer name"
          autoComplete="username"
          value={formData.username}
          onChange={handleInputChange('username')}
          invalid={actionData?.field === 'username'}
          icon={<User className="h-4 w-4" />}
        />

        <AuthField
          id="password"
          name="password"
          type="password"
          label="Password"
          placeholder="Your password"
          autoComplete="current-password"
          value={formData.password}
          onChange={handleInputChange('password')}
          invalid={actionData?.field === 'password'}
          icon={<KeyRound className="h-4 w-4" />}
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="key w-full rounded-lg bg-brass px-4 py-3.5 text-lg font-black text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
        >
          {isSubmitting ? 'Signing in' : 'Sign in'}
        </button>
      </Form>

      <p className="mt-5 text-center text-sm text-arena-ink-muted">
        No account yet?{' '}
        <Link to="/auth/register" className="font-bold text-blued underline underline-offset-2 hover:text-arena-ink">
          Create one
        </Link>
      </p>
    </>
  );
}

export default LoginForm;
