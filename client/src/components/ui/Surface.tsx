import type { ComponentPropsWithoutRef, ElementType, ReactNode } from 'react';
import { cn } from '@components/ui/cn';

const tones = {
  plain: 'bg-surface border-line',
  sunk: 'bg-surface-sunk border-line',
  accent: 'bg-accent-soft border-accent/20',
  danger: 'bg-danger-soft border-danger/25',
} as const;

const pads = {
  none: '',
  tight: 'p-3',
  base: 'p-4',
  roomy: 'p-4 sm:p-6',
} as const;

type SurfaceProps<T extends ElementType> = {
  as?: T;
  tone?: keyof typeof tones;
  pad?: keyof typeof pads;
  raised?: boolean;
  children?: ReactNode;
  className?: string;
} & Omit<ComponentPropsWithoutRef<T>, 'className' | 'children'>;

/**
 * The card shell, previously copy-pasted 24 times in 12 different class
 * orderings, which made it impossible to grep for or restyle in one place.
 */
export function Surface<T extends ElementType = 'div'>({
  as, tone = 'plain', pad = 'roomy', raised = false, className, children, ...rest
}: SurfaceProps<T>) {
  const Tag = (as ?? 'div') as ElementType;
  return (
    <Tag
      className={cn('rounded-card border', tones[tone], pads[pad], raised ? 'shadow-raised' : 'shadow-card', className)}
      {...rest}
    >
      {children}
    </Tag>
  );
}
