import { useState } from 'react';
import { Form, useActionData, useNavigation, Link } from 'react-router-dom';
import { KeyRound, User } from 'lucide-react';
import type { RegisterCredentials, AuthError } from '@store/auth/types';
import { AuthField } from '@components/auth/AuthField';

function RegisterForm() {
  const [formData, setFormData] = useState<RegisterCredentials>({
    username: '',
    password: '',
    confirmPassword: ''
  });

  const actionData = useActionData() as AuthError | undefined;
  const navigation = useNavigation();
  const isSubmitting = navigation.state === 'submitting';

  const handleInputChange = (field: keyof RegisterCredentials) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  return (
    <>
      <h1 className="display mb-1 text-xl text-arena-ink">Create an account</h1>
      <p className="mb-4 text-sm text-arena-ink-muted">
        You start with the whole roster: one of each type, and none of them beats the other two.
      </p>

      {actionData?.message && (
        <p role="alert" className="mb-4 rounded-sm border-2 border-outline bg-ruby px-3 py-2 text-sm font-bold text-white">
          {actionData.message}
        </p>
      )}

      <Form method="post" action="/auth/register" className="space-y-4">
        <AuthField
          id="reg-username"
          name="username"
          type="text"
          label="Username"
          placeholder="Pick a trainer name"
          autoComplete="username"
          value={formData.username}
          onChange={handleInputChange('username')}
          invalid={actionData?.field === 'username'}
          icon={<User className="h-4 w-4" />}
        />

        <AuthField
          id="reg-password"
          name="password"
          type="password"
          label="Password"
          placeholder="Pick a password"
          autoComplete="new-password"
          value={formData.password}
          onChange={handleInputChange('password')}
          invalid={actionData?.field === 'password'}
          icon={<KeyRound className="h-4 w-4" />}
        />

        <AuthField
          id="confirm-password"
          name="confirmPassword"
          type="password"
          label="Confirm password"
          placeholder="Type it again"
          autoComplete="new-password"
          value={formData.confirmPassword}
          onChange={handleInputChange('confirmPassword')}
          invalid={actionData?.field === 'confirmPassword'}
          icon={<KeyRound className="h-4 w-4" />}
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="key w-full rounded-lg bg-brass px-4 py-3.5 text-lg font-black text-white focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-brass/60"
        >
          {isSubmitting ? 'Creating your account' : 'Create account'}
        </button>
      </Form>

      <p className="mt-5 text-center text-sm text-arena-ink-muted">
        Already have one?{' '}
        <Link to="/auth/login" className="font-bold text-blued underline underline-offset-2 hover:text-arena-ink">
          Sign in
        </Link>
      </p>
    </>
  );
}

export default RegisterForm;
