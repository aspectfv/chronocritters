import type { ReactNode } from 'react';

/**
 * One labelled field, drawn as a milled slot rather than a bordered box. The
 * two forms had the same markup three times over, at two different widths.
 */
export function AuthField({ id, name, type, label, placeholder, value, onChange, invalid, icon, autoComplete }: {
  id: string;
  name: string;
  type: 'text' | 'password';
  label: string;
  placeholder: string;
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  invalid: boolean;
  icon: ReactNode;
  autoComplete?: string;
}) {
  return (
    <div>
      <label htmlFor={id} className="mb-1.5 block text-sm font-bold text-arena-ink">
        {label}
      </label>
      <div className="relative">
        <span className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 text-brass-ink" aria-hidden="true">
          {icon}
        </span>
        <input
          id={id}
          name={name}
          type={type}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          autoComplete={autoComplete}
          aria-invalid={invalid}
          required
          className={`well block w-full rounded-sm bg-arena-deep py-3 pl-10 pr-3 font-semibold text-arena-ink placeholder:font-normal placeholder:text-arena-ink-muted/70 focus:outline-none focus-visible:ring-4 focus-visible:ring-brass/60 ${
            invalid ? 'border-ruby' : ''
          }`}
        />
      </div>
    </div>
  );
}
