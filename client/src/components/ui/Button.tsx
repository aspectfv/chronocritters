import type { ComponentPropsWithoutRef, ElementType, ReactNode } from 'react';
import { cn } from '@components/ui/cn';

const variants = {
  primary: 'bg-accent text-white hover:bg-accent-strong disabled:bg-accent-strong disabled:opacity-60',
  secondary: 'bg-surface text-ink border border-line-strong hover:bg-surface-sunk disabled:opacity-60',
  danger: 'bg-surface text-danger border border-danger/30 hover:bg-danger-soft disabled:opacity-60',
  ghost: 'text-ink-muted hover:text-ink disabled:opacity-60',
} as const;

const sizes = {
  sm: 'text-sm px-3 py-2',
  base: 'px-4 py-3',
} as const;

type ButtonProps<T extends ElementType> = {
  as?: T;
  variant?: keyof typeof variants;
  size?: keyof typeof sizes;
  block?: boolean;
  children?: ReactNode;
  className?: string;
} & Omit<ComponentPropsWithoutRef<T>, 'className' | 'children'>;

/**
 * Replaces 15 distinct button class strings, three of which were primaries
 * that disagreed on both the green and the focus ring.
 */
export function Button<T extends ElementType = 'button'>({
  as, variant = 'primary', size = 'base', block = false, className, children, ...rest
}: ButtonProps<T>) {
  const Tag = (as ?? 'button') as ElementType;
  const typeAttr = Tag === 'button' ? { type: 'button' as const } : {};
  return (
    <Tag
      {...typeAttr}
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-control font-semibold transition-colors',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent-ring focus-visible:ring-offset-2',
        'disabled:cursor-not-allowed',
        variants[variant], sizes[size], block && 'w-full', className,
      )}
      {...rest}
    >
      {children}
    </Tag>
  );
}
