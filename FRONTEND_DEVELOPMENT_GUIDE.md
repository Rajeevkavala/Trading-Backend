# 🎨 Frontend Development Guide

## Complete Guide to Building a Trading Platform Frontend

---

## 📋 Table of Contents

1. [Technology Stack Selection](#technology-stack-selection)
2. [Project Setup](#project-setup)
3. [Folder Structure](#folder-structure)
4. [Core Features & Components](#core-features--components)
5. [State Management](#state-management)
6. [API Integration](#api-integration)
7. [Real-Time Features](#real-time-features)
8. [UI/UX Design Guidelines](#uiux-design-guidelines)
9. [Charts & Data Visualization](#charts--data-visualization)
10. [Authentication Flow](#authentication-flow)
11. [Responsive Design](#responsive-design)
12. [Testing Strategy](#testing-strategy)
13. [Performance Optimization](#performance-optimization)
14. [Deployment](#deployment)

---

## 🛠️ Technology Stack Selection

### Recommended Stack: React + TypeScript

| Category | Technology | Reason |
|----------|------------|--------|
| **Framework** | React 18+ with TypeScript | Industry standard, type safety |
| **Build Tool** | Vite | Fast HMR, modern bundling |
| **Styling** | Tailwind CSS + shadcn/ui | Rapid development, consistent design |
| **State Management** | Zustand / TanStack Query | Simple, powerful state handling |
| **Data Fetching** | TanStack Query (React Query) | Caching, background updates |
| **Charts** | TradingView Lightweight Charts / Recharts | Professional trading charts |
| **WebSocket** | Socket.IO Client / SockJS | Real-time price updates |
| **Forms** | React Hook Form + Zod | Validation, performance |
| **Routing** | React Router v6 | Navigation |
| **HTTP Client** | Axios / Ky | API requests |
| **Icons** | Lucide React | Consistent iconography |
| **Date Handling** | date-fns | Lightweight date utilities |

### Alternative Stack: Next.js

For SSR/SEO benefits, consider Next.js 14+ with App Router.

---

## 🚀 Project Setup

### 1. Initialize Project

```bash
# Create Vite project with React + TypeScript
npm create vite@latest trading-frontend -- --template react-ts
cd trading-frontend

# Install dependencies
npm install

# Install additional packages
npm install @tanstack/react-query zustand axios react-router-dom
npm install tailwindcss postcss autoprefixer -D
npm install @radix-ui/react-* lucide-react class-variance-authority clsx tailwind-merge
npm install react-hook-form @hookform/resolvers zod
npm install lightweight-charts recharts
npm install sockjs-client @stomp/stompjs
npm install date-fns
```

### 2. Configure Tailwind CSS

```bash
npx tailwindcss init -p
```

**File:** `tailwind.config.js`

```javascript
/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ["class"],
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        success: {
          DEFAULT: "#22c55e",
          foreground: "#ffffff",
        },
        danger: {
          DEFAULT: "#ef4444",
          foreground: "#ffffff",
        },
        profit: "#22c55e",
        loss: "#ef4444",
      },
      fontFamily: {
        sans: ["Inter", "sans-serif"],
        mono: ["JetBrains Mono", "monospace"],
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
}
```

### 3. Environment Configuration

**File:** `.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080/ws
VITE_APP_NAME=TradeMaster
```

**File:** `.env.production`

```env
VITE_API_BASE_URL=https://api.yourtrading.com/api
VITE_WS_URL=wss://api.yourtrading.com/ws
VITE_APP_NAME=TradeMaster
```

---

## 📁 Folder Structure

```
src/
├── api/                      # API layer
│   ├── axios.ts              # Axios instance configuration
│   ├── auth.api.ts           # Authentication API calls
│   ├── stocks.api.ts         # Stock market data API
│   ├── orders.api.ts         # Order management API
│   ├── portfolio.api.ts      # Portfolio API
│   └── watchlist.api.ts      # Watchlist API
│
├── assets/                   # Static assets
│   ├── images/
│   └── fonts/
│
├── components/               # Reusable components
│   ├── ui/                   # Base UI components (shadcn)
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   ├── card.tsx
│   │   ├── dialog.tsx
│   │   └── ...
│   │
│   ├── common/               # Common components
│   │   ├── Header.tsx
│   │   ├── Sidebar.tsx
│   │   ├── Footer.tsx
│   │   ├── Loading.tsx
│   │   ├── ErrorBoundary.tsx
│   │   └── ProtectedRoute.tsx
│   │
│   ├── charts/               # Chart components
│   │   ├── StockChart.tsx
│   │   ├── CandlestickChart.tsx
│   │   ├── PortfolioChart.tsx
│   │   └── MiniChart.tsx
│   │
│   ├── trading/              # Trading-specific components
│   │   ├── StockCard.tsx
│   │   ├── OrderForm.tsx
│   │   ├── OrderBook.tsx
│   │   ├── PriceTicker.tsx
│   │   └── MarketStatus.tsx
│   │
│   └── portfolio/            # Portfolio components
│       ├── HoldingsList.tsx
│       ├── PortfolioSummary.tsx
│       └── TransactionHistory.tsx
│
├── features/                 # Feature-based modules
│   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   └── store/
│   │
│   ├── dashboard/
│   │   ├── components/
│   │   └── hooks/
│   │
│   ├── trading/
│   │   ├── components/
│   │   ├── hooks/
│   │   └── store/
│   │
│   ├── portfolio/
│   │   ├── components/
│   │   └── hooks/
│   │
│   └── watchlist/
│       ├── components/
│       └── hooks/
│
├── hooks/                    # Custom hooks
│   ├── useAuth.ts
│   ├── useWebSocket.ts
│   ├── useStockQuote.ts
│   ├── useLocalStorage.ts
│   └── useDebounce.ts
│
├── lib/                      # Utility functions
│   ├── utils.ts
│   ├── formatters.ts
│   ├── validators.ts
│   └── constants.ts
│
├── pages/                    # Page components
│   ├── Home.tsx
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── Dashboard.tsx
│   ├── Trading.tsx
│   ├── Portfolio.tsx
│   ├── Watchlist.tsx
│   ├── Orders.tsx
│   ├── Profile.tsx
│   └── NotFound.tsx
│
├── services/                 # Business logic services
│   ├── websocket.service.ts
│   └── notification.service.ts
│
├── store/                    # Global state management
│   ├── authStore.ts
│   ├── uiStore.ts
│   └── marketStore.ts
│
├── types/                    # TypeScript types
│   ├── auth.types.ts
│   ├── stock.types.ts
│   ├── order.types.ts
│   ├── portfolio.types.ts
│   └── api.types.ts
│
├── App.tsx                   # Root component
├── main.tsx                  # Entry point
├── index.css                 # Global styles
└── vite-env.d.ts             # Vite types
```

---

## 🎯 Core Features & Components

### 1. Authentication Components

**File:** `src/pages/Login.tsx`

```tsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuthStore } from '@/store/authStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Eye, EyeOff, TrendingUp, Loader2 } from 'lucide-react';

const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Username or email is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function Login() {
  const navigate = useNavigate();
  const { login, isLoading, error } = useAuthStore();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data);
      navigate('/dashboard');
    } catch (error) {
      // Error handled by store
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 p-4">
      <Card className="w-full max-w-md bg-gray-800/50 border-gray-700 backdrop-blur-sm">
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            <div className="p-3 bg-primary/10 rounded-full">
              <TrendingUp className="h-8 w-8 text-primary" />
            </div>
          </div>
          <CardTitle className="text-2xl text-white">Welcome Back</CardTitle>
          <CardDescription className="text-gray-400">
            Sign in to your trading account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {error && (
              <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg">
                <p className="text-sm text-red-400">{error}</p>
              </div>
            )}

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Username or Email</label>
              <Input
                {...register('usernameOrEmail')}
                placeholder="Enter username or email"
                className="bg-gray-700/50 border-gray-600 text-white"
              />
              {errors.usernameOrEmail && (
                <p className="text-sm text-red-400">{errors.usernameOrEmail.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Password</label>
              <div className="relative">
                <Input
                  {...register('password')}
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter password"
                  className="bg-gray-700/50 border-gray-600 text-white pr-10"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && (
                <p className="text-sm text-red-400">{errors.password.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Signing in...
                </>
              ) : (
                'Sign In'
              )}
            </Button>

            <p className="text-center text-gray-400 text-sm">
              Don't have an account?{' '}
              <Link to="/register" className="text-primary hover:underline">
                Create one
              </Link>
            </p>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
```

**File:** `src/pages/Register.tsx`

```tsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuthStore } from '@/store/authStore';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Eye, EyeOff, TrendingUp, Loader2 } from 'lucide-react';

const registerSchema = z.object({
  username: z.string()
    .min(3, 'Username must be at least 3 characters')
    .max(20, 'Username must be at most 20 characters')
    .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers, and underscores'),
  email: z.string().email('Invalid email address'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
    .regex(/[0-9]/, 'Password must contain at least one number'),
  confirmPassword: z.string(),
  fullName: z.string().min(2, 'Full name is required'),
  phoneNumber: z.string().optional(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export default function Register() {
  const navigate = useNavigate();
  const { register: registerUser, isLoading, error } = useAuthStore();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      await registerUser(data);
      navigate('/dashboard');
    } catch (error) {
      // Error handled by store
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 p-4">
      <Card className="w-full max-w-md bg-gray-800/50 border-gray-700 backdrop-blur-sm">
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            <div className="p-3 bg-primary/10 rounded-full">
              <TrendingUp className="h-8 w-8 text-primary" />
            </div>
          </div>
          <CardTitle className="text-2xl text-white">Create Account</CardTitle>
          <CardDescription className="text-gray-400">
            Start your trading journey today
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {error && (
              <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg">
                <p className="text-sm text-red-400">{error}</p>
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm text-gray-300">Username</label>
                <Input
                  {...register('username')}
                  placeholder="username"
                  className="bg-gray-700/50 border-gray-600 text-white"
                />
                {errors.username && (
                  <p className="text-xs text-red-400">{errors.username.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <label className="text-sm text-gray-300">Full Name</label>
                <Input
                  {...register('fullName')}
                  placeholder="John Doe"
                  className="bg-gray-700/50 border-gray-600 text-white"
                />
                {errors.fullName && (
                  <p className="text-xs text-red-400">{errors.fullName.message}</p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Email</label>
              <Input
                {...register('email')}
                type="email"
                placeholder="you@example.com"
                className="bg-gray-700/50 border-gray-600 text-white"
              />
              {errors.email && (
                <p className="text-sm text-red-400">{errors.email.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Phone (Optional)</label>
              <Input
                {...register('phoneNumber')}
                placeholder="+91 9876543210"
                className="bg-gray-700/50 border-gray-600 text-white"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Password</label>
              <div className="relative">
                <Input
                  {...register('password')}
                  type={showPassword ? 'text' : 'password'}
                  placeholder="••••••••"
                  className="bg-gray-700/50 border-gray-600 text-white pr-10"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && (
                <p className="text-sm text-red-400">{errors.password.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <label className="text-sm text-gray-300">Confirm Password</label>
              <Input
                {...register('confirmPassword')}
                type="password"
                placeholder="••••••••"
                className="bg-gray-700/50 border-gray-600 text-white"
              />
              {errors.confirmPassword && (
                <p className="text-sm text-red-400">{errors.confirmPassword.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Creating Account...
                </>
              ) : (
                'Create Account'
              )}
            </Button>

            <p className="text-center text-gray-400 text-sm">
              Already have an account?{' '}
              <Link to="/login" className="text-primary hover:underline">
                Sign in
              </Link>
            </p>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
```

### 2. Dashboard Page

**File:** `src/pages/Dashboard.tsx`

```tsx
import { useEffect } from 'react';
import { useDashboard } from '@/features/dashboard/hooks/useDashboard';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { PortfolioSummary } from '@/components/portfolio/PortfolioSummary';
import { MarketIndices } from '@/components/trading/MarketIndices';
import { TopMovers } from '@/components/trading/TopMovers';
import { WatchlistWidget } from '@/components/watchlist/WatchlistWidget';
import { RecentTransactions } from '@/components/portfolio/RecentTransactions';
import { PortfolioChart } from '@/components/charts/PortfolioChart';
import { 
  Wallet, TrendingUp, TrendingDown, Activity, 
  ArrowUpRight, ArrowDownRight 
} from 'lucide-react';

export default function Dashboard() {
  const { 
    portfolio, 
    marketIndices, 
    topGainers, 
    topLosers,
    recentTransactions,
    isLoading 
  } = useDashboard();

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  return (
    <div className="p-6 space-y-6">
      {/* Header Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatsCard
          title="Portfolio Value"
          value={portfolio.totalValue}
          change={portfolio.dayChange}
          changePercent={portfolio.dayChangePercent}
          icon={<Wallet className="h-5 w-5" />}
          format="currency"
        />
        <StatsCard
          title="Total Investment"
          value={portfolio.totalInvestment}
          icon={<Activity className="h-5 w-5" />}
          format="currency"
        />
        <StatsCard
          title="Total P&L"
          value={portfolio.totalProfitLoss}
          changePercent={portfolio.totalProfitLossPercent}
          icon={portfolio.totalProfitLoss >= 0 ? 
            <TrendingUp className="h-5 w-5" /> : 
            <TrendingDown className="h-5 w-5" />}
          format="currency"
        />
        <StatsCard
          title="Available Cash"
          value={portfolio.availableCash}
          icon={<Wallet className="h-5 w-5" />}
          format="currency"
        />
      </div>

      {/* Market Indices */}
      <Card className="bg-gray-800/50 border-gray-700">
        <CardHeader>
          <CardTitle className="text-white">Market Overview</CardTitle>
        </CardHeader>
        <CardContent>
          <MarketIndices indices={marketIndices} />
        </CardContent>
      </Card>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Portfolio Performance Chart */}
        <div className="lg:col-span-2">
          <Card className="bg-gray-800/50 border-gray-700 h-full">
            <CardHeader>
              <CardTitle className="text-white">Portfolio Performance</CardTitle>
            </CardHeader>
            <CardContent>
              <PortfolioChart data={portfolio.performanceHistory} />
            </CardContent>
          </Card>
        </div>

        {/* Watchlist */}
        <Card className="bg-gray-800/50 border-gray-700">
          <CardHeader>
            <CardTitle className="text-white">Watchlist</CardTitle>
          </CardHeader>
          <CardContent>
            <WatchlistWidget />
          </CardContent>
        </Card>
      </div>

      {/* Top Movers */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="bg-gray-800/50 border-gray-700">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-white flex items-center gap-2">
              <TrendingUp className="h-5 w-5 text-green-500" />
              Top Gainers
            </CardTitle>
          </CardHeader>
          <CardContent>
            <TopMovers stocks={topGainers} type="gainers" />
          </CardContent>
        </Card>

        <Card className="bg-gray-800/50 border-gray-700">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-white flex items-center gap-2">
              <TrendingDown className="h-5 w-5 text-red-500" />
              Top Losers
            </CardTitle>
          </CardHeader>
          <CardContent>
            <TopMovers stocks={topLosers} type="losers" />
          </CardContent>
        </Card>
      </div>

      {/* Recent Transactions */}
      <Card className="bg-gray-800/50 border-gray-700">
        <CardHeader>
          <CardTitle className="text-white">Recent Transactions</CardTitle>
        </CardHeader>
        <CardContent>
          <RecentTransactions transactions={recentTransactions} />
        </CardContent>
      </Card>
    </div>
  );
}

// Stats Card Component
function StatsCard({ title, value, change, changePercent, icon, format }) {
  const formatValue = (val: number, fmt: string) => {
    if (fmt === 'currency') {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2,
      }).format(val);
    }
    return val.toLocaleString();
  };

  const isPositive = changePercent ? changePercent >= 0 : change >= 0;

  return (
    <Card className="bg-gray-800/50 border-gray-700">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="p-2 bg-primary/10 rounded-lg text-primary">
            {icon}
          </div>
          {changePercent !== undefined && (
            <div className={`flex items-center gap-1 text-sm ${
              isPositive ? 'text-green-500' : 'text-red-500'
            }`}>
              {isPositive ? <ArrowUpRight size={16} /> : <ArrowDownRight size={16} />}
              {Math.abs(changePercent).toFixed(2)}%
            </div>
          )}
        </div>
        <div className="mt-4">
          <p className="text-sm text-gray-400">{title}</p>
          <p className={`text-2xl font-bold ${
            change !== undefined 
              ? (change >= 0 ? 'text-green-400' : 'text-red-400')
              : 'text-white'
          }`}>
            {formatValue(value, format)}
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
```

### 3. Trading Page

**File:** `src/pages/Trading.tsx`

```tsx
import { useState, useEffect } from 'react';
import { useParams, useSearchParams } from 'react-router-dom';
import { useStockQuote } from '@/hooks/useStockQuote';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { StockChart } from '@/components/charts/StockChart';
import { OrderForm } from '@/components/trading/OrderForm';
import { StockInfo } from '@/components/trading/StockInfo';
import { StockSearch } from '@/components/trading/StockSearch';
import { OrderBook } from '@/components/trading/OrderBook';
import { MarketDepth } from '@/components/trading/MarketDepth';

export default function Trading() {
  const { symbol } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const market = searchParams.get('market') || 'NSE';
  const [selectedSymbol, setSelectedSymbol] = useState(symbol || 'RELIANCE');
  const [timeFrame, setTimeFrame] = useState('1D');

  const { quote, isLoading, error } = useStockQuote(selectedSymbol, market);

  return (
    <div className="p-6 space-y-6">
      {/* Stock Search */}
      <StockSearch 
        onSelect={(stock) => {
          setSelectedSymbol(stock.symbol);
          setSearchParams({ market: stock.market });
        }}
      />

      {/* Stock Header */}
      {quote && (
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold text-white">{quote.symbol}</h1>
              <span className="px-2 py-1 bg-gray-700 rounded text-sm text-gray-300">
                {quote.exchange}
              </span>
            </div>
            <p className="text-gray-400">{quote.name}</p>
          </div>
          <div className="text-right">
            <p className="text-3xl font-bold text-white">
              ₹{quote.currentPrice.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
            </p>
            <p className={`flex items-center justify-end gap-1 ${
              quote.change >= 0 ? 'text-green-500' : 'text-red-500'
            }`}>
              {quote.change >= 0 ? '+' : ''}{quote.change.toFixed(2)} 
              ({quote.changePercent >= 0 ? '+' : ''}{quote.changePercent.toFixed(2)}%)
            </p>
          </div>
        </div>
      )}

      {/* Main Trading Interface */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Chart Section */}
        <div className="lg:col-span-3 space-y-4">
          <Card className="bg-gray-800/50 border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-white">Price Chart</CardTitle>
              <div className="flex gap-2">
                {['1D', '1W', '1M', '3M', '1Y', 'ALL'].map((tf) => (
                  <button
                    key={tf}
                    onClick={() => setTimeFrame(tf)}
                    className={`px-3 py-1 rounded text-sm ${
                      timeFrame === tf
                        ? 'bg-primary text-white'
                        : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                    }`}
                  >
                    {tf}
                  </button>
                ))}
              </div>
            </CardHeader>
            <CardContent>
              <StockChart 
                symbol={selectedSymbol} 
                market={market} 
                timeFrame={timeFrame}
              />
            </CardContent>
          </Card>

          {/* Stock Info Tabs */}
          <Card className="bg-gray-800/50 border-gray-700">
            <Tabs defaultValue="overview">
              <CardHeader>
                <TabsList className="bg-gray-700/50">
                  <TabsTrigger value="overview">Overview</TabsTrigger>
                  <TabsTrigger value="technicals">Technicals</TabsTrigger>
                  <TabsTrigger value="financials">Financials</TabsTrigger>
                  <TabsTrigger value="news">News</TabsTrigger>
                </TabsList>
              </CardHeader>
              <CardContent>
                <TabsContent value="overview">
                  <StockInfo quote={quote} />
                </TabsContent>
                <TabsContent value="technicals">
                  {/* Technical indicators */}
                </TabsContent>
                <TabsContent value="financials">
                  {/* Financial data */}
                </TabsContent>
                <TabsContent value="news">
                  {/* Related news */}
                </TabsContent>
              </CardContent>
            </Tabs>
          </Card>
        </div>

        {/* Order Form Section */}
        <div className="space-y-4">
          <Card className="bg-gray-800/50 border-gray-700">
            <CardHeader>
              <CardTitle className="text-white">Place Order</CardTitle>
            </CardHeader>
            <CardContent>
              <OrderForm 
                symbol={selectedSymbol} 
                market={market}
                currentPrice={quote?.currentPrice} 
              />
            </CardContent>
          </Card>

          {/* Market Depth */}
          <Card className="bg-gray-800/50 border-gray-700">
            <CardHeader>
              <CardTitle className="text-white">Market Depth</CardTitle>
            </CardHeader>
            <CardContent>
              <MarketDepth symbol={selectedSymbol} />
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
```

### 4. Order Form Component

**File:** `src/components/trading/OrderForm.tsx`

```tsx
import { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ordersApi } from '@/api/orders.api';
import { toast } from 'sonner';
import { Loader2 } from 'lucide-react';

const orderSchema = z.object({
  side: z.enum(['BUY', 'SELL']),
  orderType: z.enum(['MARKET', 'LIMIT', 'STOP_LOSS', 'STOP_LIMIT']),
  quantity: z.number().min(1, 'Minimum quantity is 1').max(100000),
  limitPrice: z.number().positive().optional(),
  stopPrice: z.number().positive().optional(),
  validity: z.enum(['DAY', 'GTC', 'IOC']),
}).refine((data) => {
  if (data.orderType === 'LIMIT' || data.orderType === 'STOP_LIMIT') {
    return data.limitPrice !== undefined;
  }
  return true;
}, {
  message: 'Limit price is required for this order type',
  path: ['limitPrice'],
}).refine((data) => {
  if (data.orderType === 'STOP_LOSS' || data.orderType === 'STOP_LIMIT') {
    return data.stopPrice !== undefined;
  }
  return true;
}, {
  message: 'Stop price is required for this order type',
  path: ['stopPrice'],
});

type OrderFormData = z.infer<typeof orderSchema>;

interface OrderFormProps {
  symbol: string;
  market: string;
  currentPrice?: number;
}

export function OrderForm({ symbol, market, currentPrice }: OrderFormProps) {
  const [activeTab, setActiveTab] = useState<'BUY' | 'SELL'>('BUY');
  const queryClient = useQueryClient();

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
    reset,
  } = useForm<OrderFormData>({
    resolver: zodResolver(orderSchema),
    defaultValues: {
      side: 'BUY',
      orderType: 'MARKET',
      quantity: 1,
      validity: 'DAY',
    },
  });

  const orderType = watch('orderType');
  const quantity = watch('quantity') || 0;

  const placeOrderMutation = useMutation({
    mutationFn: (data: OrderFormData) => ordersApi.placeOrder({
      ...data,
      symbol,
      market,
    }),
    onSuccess: (order) => {
      toast.success(`Order placed successfully! Order ID: ${order.orderId}`);
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['portfolio'] });
      reset();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to place order');
    },
  });

  const onSubmit = (data: OrderFormData) => {
    placeOrderMutation.mutate({
      ...data,
      side: activeTab,
    });
  };

  const estimatedValue = currentPrice ? (currentPrice * quantity).toFixed(2) : '0.00';

  return (
    <div className="space-y-4">
      {/* Buy/Sell Tabs */}
      <Tabs value={activeTab} onValueChange={(v) => setActiveTab(v as 'BUY' | 'SELL')}>
        <TabsList className="w-full grid grid-cols-2">
          <TabsTrigger 
            value="BUY" 
            className="data-[state=active]:bg-green-600 data-[state=active]:text-white"
          >
            BUY
          </TabsTrigger>
          <TabsTrigger 
            value="SELL"
            className="data-[state=active]:bg-red-600 data-[state=active]:text-white"
          >
            SELL
          </TabsTrigger>
        </TabsList>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-4">
          {/* Order Type */}
          <div className="space-y-2">
            <Label className="text-gray-300">Order Type</Label>
            <Controller
              name="orderType"
              control={control}
              render={({ field }) => (
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <SelectTrigger className="bg-gray-700/50 border-gray-600 text-white">
                    <SelectValue placeholder="Select order type" />
                  </SelectTrigger>
                  <SelectContent className="bg-gray-800 border-gray-700">
                    <SelectItem value="MARKET">Market</SelectItem>
                    <SelectItem value="LIMIT">Limit</SelectItem>
                    <SelectItem value="STOP_LOSS">Stop Loss</SelectItem>
                    <SelectItem value="STOP_LIMIT">Stop Limit</SelectItem>
                  </SelectContent>
                </Select>
              )}
            />
          </div>

          {/* Quantity */}
          <div className="space-y-2">
            <Label className="text-gray-300">Quantity</Label>
            <Input
              type="number"
              {...register('quantity', { valueAsNumber: true })}
              className="bg-gray-700/50 border-gray-600 text-white"
              min={1}
            />
            {errors.quantity && (
              <p className="text-sm text-red-400">{errors.quantity.message}</p>
            )}
          </div>

          {/* Limit Price */}
          {(orderType === 'LIMIT' || orderType === 'STOP_LIMIT') && (
            <div className="space-y-2">
              <Label className="text-gray-300">Limit Price</Label>
              <Input
                type="number"
                step="0.01"
                {...register('limitPrice', { valueAsNumber: true })}
                className="bg-gray-700/50 border-gray-600 text-white"
                placeholder={currentPrice?.toString()}
              />
              {errors.limitPrice && (
                <p className="text-sm text-red-400">{errors.limitPrice.message}</p>
              )}
            </div>
          )}

          {/* Stop Price */}
          {(orderType === 'STOP_LOSS' || orderType === 'STOP_LIMIT') && (
            <div className="space-y-2">
              <Label className="text-gray-300">Stop Price</Label>
              <Input
                type="number"
                step="0.01"
                {...register('stopPrice', { valueAsNumber: true })}
                className="bg-gray-700/50 border-gray-600 text-white"
              />
              {errors.stopPrice && (
                <p className="text-sm text-red-400">{errors.stopPrice.message}</p>
              )}
            </div>
          )}

          {/* Validity */}
          <div className="space-y-2">
            <Label className="text-gray-300">Validity</Label>
            <Controller
              name="validity"
              control={control}
              render={({ field }) => (
                <RadioGroup
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                  className="flex gap-4"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="DAY" id="day" />
                    <Label htmlFor="day" className="text-gray-300">Day</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="GTC" id="gtc" />
                    <Label htmlFor="gtc" className="text-gray-300">GTC</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="IOC" id="ioc" />
                    <Label htmlFor="ioc" className="text-gray-300">IOC</Label>
                  </div>
                </RadioGroup>
              )}
            />
          </div>

          {/* Order Summary */}
          <div className="p-4 bg-gray-700/30 rounded-lg space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Current Price</span>
              <span className="text-white">₹{currentPrice?.toLocaleString('en-IN') || '-'}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-400">Quantity</span>
              <span className="text-white">{quantity}</span>
            </div>
            <div className="flex justify-between font-semibold border-t border-gray-600 pt-2">
              <span className="text-gray-300">Estimated Value</span>
              <span className="text-white">₹{parseFloat(estimatedValue).toLocaleString('en-IN')}</span>
            </div>
          </div>

          {/* Submit Button */}
          <Button
            type="submit"
            className={`w-full ${
              activeTab === 'BUY' 
                ? 'bg-green-600 hover:bg-green-700' 
                : 'bg-red-600 hover:bg-red-700'
            }`}
            disabled={placeOrderMutation.isPending}
          >
            {placeOrderMutation.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Placing Order...
              </>
            ) : (
              `${activeTab} ${symbol}`
            )}
          </Button>
        </form>
      </Tabs>
    </div>
  );
}
```

---

## 🔄 State Management

### 1. Auth Store (Zustand)

**File:** `src/store/authStore.ts`

```typescript
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { authApi } from '@/api/auth.api';
import { User, AuthTokens, LoginRequest, RegisterRequest } from '@/types/auth.types';

interface AuthState {
  user: User | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
  clearError: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      tokens: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      login: async (credentials) => {
        set({ isLoading: true, error: null });
        try {
          const response = await authApi.login(credentials);
          set({
            user: response.user,
            tokens: {
              accessToken: response.accessToken,
              refreshToken: response.refreshToken,
              expiresIn: response.expiresIn,
            },
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            error: error.response?.data?.message || 'Login failed',
            isLoading: false,
          });
          throw error;
        }
      },

      register: async (data) => {
        set({ isLoading: true, error: null });
        try {
          const response = await authApi.register(data);
          set({
            user: response.user,
            tokens: {
              accessToken: response.accessToken,
              refreshToken: response.refreshToken,
              expiresIn: response.expiresIn,
            },
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            error: error.response?.data?.message || 'Registration failed',
            isLoading: false,
          });
          throw error;
        }
      },

      logout: async () => {
        try {
          const { tokens } = get();
          if (tokens?.accessToken) {
            await authApi.logout(tokens.accessToken);
          }
        } finally {
          set({
            user: null,
            tokens: null,
            isAuthenticated: false,
          });
        }
      },

      refreshToken: async () => {
        const { tokens } = get();
        if (!tokens?.refreshToken) {
          throw new Error('No refresh token');
        }

        try {
          const response = await authApi.refreshToken(tokens.refreshToken);
          set({
            tokens: {
              accessToken: response.accessToken,
              refreshToken: response.refreshToken,
              expiresIn: response.expiresIn,
            },
          });
        } catch (error) {
          // Refresh failed, logout user
          get().logout();
          throw error;
        }
      },

      clearError: () => set({ error: null }),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        tokens: state.tokens,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
```

### 2. Market Store

**File:** `src/store/marketStore.ts`

```typescript
import { create } from 'zustand';
import { subscribeWithSelector } from 'zustand/middleware';
import { StockQuote, MarketIndex } from '@/types/stock.types';

interface MarketState {
  quotes: Map<string, StockQuote>;
  indices: MarketIndex[];
  subscribedSymbols: Set<string>;
  isMarketOpen: boolean;
  
  updateQuote: (symbol: string, quote: StockQuote) => void;
  updateQuotes: (quotes: StockQuote[]) => void;
  setIndices: (indices: MarketIndex[]) => void;
  subscribeToSymbol: (symbol: string) => void;
  unsubscribeFromSymbol: (symbol: string) => void;
  setMarketOpen: (isOpen: boolean) => void;
}

export const useMarketStore = create<MarketState>()(
  subscribeWithSelector((set, get) => ({
    quotes: new Map(),
    indices: [],
    subscribedSymbols: new Set(),
    isMarketOpen: true,

    updateQuote: (symbol, quote) => {
      set((state) => {
        const newQuotes = new Map(state.quotes);
        newQuotes.set(symbol, quote);
        return { quotes: newQuotes };
      });
    },

    updateQuotes: (quotes) => {
      set((state) => {
        const newQuotes = new Map(state.quotes);
        quotes.forEach((quote) => {
          newQuotes.set(quote.symbol, quote);
        });
        return { quotes: newQuotes };
      });
    },

    setIndices: (indices) => set({ indices }),

    subscribeToSymbol: (symbol) => {
      set((state) => {
        const newSubscribed = new Set(state.subscribedSymbols);
        newSubscribed.add(symbol);
        return { subscribedSymbols: newSubscribed };
      });
    },

    unsubscribeFromSymbol: (symbol) => {
      set((state) => {
        const newSubscribed = new Set(state.subscribedSymbols);
        newSubscribed.delete(symbol);
        return { subscribedSymbols: newSubscribed };
      });
    },

    setMarketOpen: (isOpen) => set({ isMarketOpen: isOpen }),
  }))
);

// Selector for getting a specific quote
export const useStockQuoteFromStore = (symbol: string) => 
  useMarketStore((state) => state.quotes.get(symbol));
```

---

## 🌐 API Integration

### 1. Axios Configuration

**File:** `src/api/axios.ts`

```typescript
import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { useAuthStore } from '@/store/authStore';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - add auth token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const tokens = useAuthStore.getState().tokens;
    if (tokens?.accessToken) {
      config.headers.Authorization = `Bearer ${tokens.accessToken}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor - handle token refresh
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };
    
    // If 401 and not already retried, try to refresh token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        await useAuthStore.getState().refreshToken();
        const newTokens = useAuthStore.getState().tokens;
        
        if (newTokens?.accessToken) {
          originalRequest.headers.Authorization = `Bearer ${newTokens.accessToken}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed, redirect to login
        useAuthStore.getState().logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### 2. API Modules

**File:** `src/api/auth.api.ts`

```typescript
import api from './axios';
import { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth.types';

export const authApi = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/refresh', { refreshToken });
    return response.data;
  },

  logout: async (token: string): Promise<void> => {
    await api.post('/auth/logout', null, {
      headers: { Authorization: `Bearer ${token}` },
    });
  },
};
```

**File:** `src/api/stocks.api.ts`

```typescript
import api from './axios';
import { StockQuote, HistoricalData, StockSearchResult, MarketIndex } from '@/types/stock.types';

export const stocksApi = {
  getQuote: async (symbol: string, market: string = 'NSE'): Promise<StockQuote> => {
    const response = await api.get<StockQuote>(`/stocks/quote/${symbol}`, {
      params: { market },
    });
    return response.data;
  },

  getQuotes: async (symbols: string[], market: string = 'NSE'): Promise<StockQuote[]> => {
    const response = await api.get<StockQuote[]>('/stocks/quotes', {
      params: { symbols: symbols.join(','), market },
    });
    return response.data;
  },

  getHistoricalData: async (
    symbol: string,
    market: string,
    timeFrame: string,
    limit: number = 100
  ): Promise<HistoricalData[]> => {
    const response = await api.get<HistoricalData[]>(`/stocks/history/${symbol}`, {
      params: { market, timeFrame, limit },
    });
    return response.data;
  },

  searchStocks: async (query: string, market?: string): Promise<StockSearchResult[]> => {
    const response = await api.get<StockSearchResult[]>('/stocks/search', {
      params: { query, market },
    });
    return response.data;
  },

  getMarketIndices: async (market: string = 'NSE'): Promise<MarketIndex[]> => {
    const response = await api.get<MarketIndex[]>('/stocks/indices', {
      params: { market },
    });
    return response.data;
  },

  getTopGainers: async (market: string = 'NSE', limit: number = 10): Promise<StockQuote[]> => {
    const response = await api.get<StockQuote[]>('/stocks/top-gainers', {
      params: { market, limit },
    });
    return response.data;
  },

  getTopLosers: async (market: string = 'NSE', limit: number = 10): Promise<StockQuote[]> => {
    const response = await api.get<StockQuote[]>('/stocks/top-losers', {
      params: { market, limit },
    });
    return response.data;
  },
};
```

**File:** `src/api/orders.api.ts`

```typescript
import api from './axios';
import { Order, PlaceOrderRequest, ModifyOrderRequest, OrderStatus } from '@/types/order.types';

export const ordersApi = {
  placeOrder: async (data: PlaceOrderRequest): Promise<Order> => {
    const response = await api.post<Order>('/orders', data);
    return response.data;
  },

  getOrders: async (status?: OrderStatus, page: number = 0, size: number = 20): Promise<{
    content: Order[];
    totalElements: number;
    totalPages: number;
  }> => {
    const response = await api.get('/orders', {
      params: { status, page, size },
    });
    return response.data;
  },

  getOrder: async (orderId: string): Promise<Order> => {
    const response = await api.get<Order>(`/orders/${orderId}`);
    return response.data;
  },

  cancelOrder: async (orderId: string): Promise<Order> => {
    const response = await api.delete<Order>(`/orders/${orderId}`);
    return response.data;
  },

  modifyOrder: async (orderId: string, data: ModifyOrderRequest): Promise<Order> => {
    const response = await api.put<Order>(`/orders/${orderId}`, data);
    return response.data;
  },

  getPendingOrders: async (): Promise<Order[]> => {
    const response = await api.get<Order[]>('/orders/pending');
    return response.data;
  },
};
```

---

## ⚡ Real-Time Features

### 1. WebSocket Service

**File:** `src/services/websocket.service.ts`

```typescript
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useMarketStore } from '@/store/marketStore';
import { useAuthStore } from '@/store/authStore';
import { StockQuote } from '@/types/stock.types';

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, any> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  connect() {
    const wsUrl = import.meta.env.VITE_WS_URL;
    const tokens = useAuthStore.getState().tokens;

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {
        Authorization: tokens?.accessToken ? `Bearer ${tokens.accessToken}` : '',
      },
      debug: (str) => {
        if (import.meta.env.DEV) {
          console.log('STOMP: ' + str);
        }
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: this.onConnect.bind(this),
      onDisconnect: this.onDisconnect.bind(this),
      onStompError: this.onError.bind(this),
    });

    this.client.activate();
  }

  private onConnect() {
    console.log('WebSocket connected');
    this.reconnectAttempts = 0;
    
    // Re-subscribe to previously subscribed symbols
    const { subscribedSymbols } = useMarketStore.getState();
    subscribedSymbols.forEach((symbol) => {
      this.subscribeToSymbol(symbol);
    });
  }

  private onDisconnect() {
    console.log('WebSocket disconnected');
  }

  private onError(frame: any) {
    console.error('WebSocket error:', frame);
    this.reconnectAttempts++;
    
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnect attempts reached');
      this.disconnect();
    }
  }

  subscribeToSymbol(symbol: string) {
    if (!this.client?.connected) {
      console.warn('WebSocket not connected');
      return;
    }

    if (this.subscriptions.has(symbol)) {
      return; // Already subscribed
    }

    const subscription = this.client.subscribe(
      `/topic/prices/${symbol}`,
      (message: IMessage) => {
        try {
          const quote: StockQuote = JSON.parse(message.body);
          useMarketStore.getState().updateQuote(symbol, quote);
        } catch (error) {
          console.error('Error parsing quote:', error);
        }
      }
    );

    this.subscriptions.set(symbol, subscription);
    useMarketStore.getState().subscribeToSymbol(symbol);

    // Notify server about subscription
    this.client.publish({
      destination: '/app/subscribe',
      body: JSON.stringify({ symbols: [symbol] }),
    });
  }

  unsubscribeFromSymbol(symbol: string) {
    const subscription = this.subscriptions.get(symbol);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(symbol);
      useMarketStore.getState().unsubscribeFromSymbol(symbol);

      // Notify server
      this.client?.publish({
        destination: '/app/unsubscribe',
        body: JSON.stringify({ symbols: [symbol] }),
      });
    }
  }

  subscribeToUserUpdates(userId: string, callback: (data: any) => void) {
    if (!this.client?.connected) return;

    return this.client.subscribe(
      `/user/${userId}/queue/orders`,
      (message: IMessage) => {
        try {
          const data = JSON.parse(message.body);
          callback(data);
        } catch (error) {
          console.error('Error parsing user update:', error);
        }
      }
    );
  }

  disconnect() {
    if (this.client) {
      this.subscriptions.forEach((sub) => sub.unsubscribe());
      this.subscriptions.clear();
      this.client.deactivate();
      this.client = null;
    }
  }

  isConnected(): boolean {
    return this.client?.connected ?? false;
  }
}

export const websocketService = new WebSocketService();
```

### 2. WebSocket Hook

**File:** `src/hooks/useWebSocket.ts`

```typescript
import { useEffect, useCallback } from 'react';
import { websocketService } from '@/services/websocket.service';
import { useAuthStore } from '@/store/authStore';

export function useWebSocket() {
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    if (isAuthenticated) {
      websocketService.connect();
    }

    return () => {
      websocketService.disconnect();
    };
  }, [isAuthenticated]);

  const subscribeToSymbol = useCallback((symbol: string) => {
    websocketService.subscribeToSymbol(symbol);
  }, []);

  const unsubscribeFromSymbol = useCallback((symbol: string) => {
    websocketService.unsubscribeFromSymbol(symbol);
  }, []);

  return {
    subscribeToSymbol,
    unsubscribeFromSymbol,
    isConnected: websocketService.isConnected(),
  };
}
```

### 3. Real-Time Stock Quote Hook

**File:** `src/hooks/useStockQuote.ts`

```typescript
import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { stocksApi } from '@/api/stocks.api';
import { useMarketStore, useStockQuoteFromStore } from '@/store/marketStore';
import { useWebSocket } from './useWebSocket';

export function useStockQuote(symbol: string, market: string = 'NSE') {
  const { subscribeToSymbol, unsubscribeFromSymbol } = useWebSocket();
  const storeQuote = useStockQuoteFromStore(symbol);

  // Initial fetch with React Query
  const { data: initialQuote, isLoading, error, refetch } = useQuery({
    queryKey: ['stockQuote', symbol, market],
    queryFn: () => stocksApi.getQuote(symbol, market),
    staleTime: 10000, // Consider stale after 10 seconds
    gcTime: 60000, // Keep in cache for 1 minute
  });

  // Subscribe to WebSocket updates
  useEffect(() => {
    if (symbol) {
      subscribeToSymbol(symbol);
    }

    return () => {
      if (symbol) {
        unsubscribeFromSymbol(symbol);
      }
    };
  }, [symbol, subscribeToSymbol, unsubscribeFromSymbol]);

  // Prefer WebSocket quote, fallback to API quote
  const quote = storeQuote || initialQuote;

  return {
    quote,
    isLoading,
    error,
    refetch,
  };
}
```

---

## 📊 Charts & Data Visualization

### 1. TradingView Lightweight Charts Integration

**File:** `src/components/charts/StockChart.tsx`

```tsx
import { useEffect, useRef } from 'react';
import { createChart, IChartApi, ISeriesApi, CandlestickSeries, HistogramSeries } from 'lightweight-charts';
import { useQuery } from '@tanstack/react-query';
import { stocksApi } from '@/api/stocks.api';
import { HistoricalData } from '@/types/stock.types';

interface StockChartProps {
  symbol: string;
  market: string;
  timeFrame: string;
}

export function StockChart({ symbol, market, timeFrame }: StockChartProps) {
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<IChartApi | null>(null);
  const candlestickSeriesRef = useRef<ISeriesApi<'Candlestick'> | null>(null);
  const volumeSeriesRef = useRef<ISeriesApi<'Histogram'> | null>(null);

  const { data: historicalData, isLoading } = useQuery({
    queryKey: ['historicalData', symbol, market, timeFrame],
    queryFn: () => stocksApi.getHistoricalData(symbol, market, timeFrame, 200),
  });

  // Initialize chart
  useEffect(() => {
    if (!chartContainerRef.current) return;

    const chart = createChart(chartContainerRef.current, {
      width: chartContainerRef.current.clientWidth,
      height: 400,
      layout: {
        background: { type: 'solid', color: 'transparent' },
        textColor: '#9ca3af',
      },
      grid: {
        vertLines: { color: '#374151' },
        horzLines: { color: '#374151' },
      },
      crosshair: {
        mode: 1,
      },
      rightPriceScale: {
        borderColor: '#374151',
      },
      timeScale: {
        borderColor: '#374151',
        timeVisible: true,
        secondsVisible: false,
      },
    });

    const candlestickSeries = chart.addSeries(CandlestickSeries, {
      upColor: '#22c55e',
      downColor: '#ef4444',
      borderUpColor: '#22c55e',
      borderDownColor: '#ef4444',
      wickUpColor: '#22c55e',
      wickDownColor: '#ef4444',
    });

    const volumeSeries = chart.addSeries(HistogramSeries, {
      color: '#6366f1',
      priceFormat: {
        type: 'volume',
      },
      priceScaleId: '',
    });

    volumeSeries.priceScale().applyOptions({
      scaleMargins: {
        top: 0.8,
        bottom: 0,
      },
    });

    chartRef.current = chart;
    candlestickSeriesRef.current = candlestickSeries;
    volumeSeriesRef.current = volumeSeries;

    // Handle resize
    const handleResize = () => {
      if (chartContainerRef.current && chartRef.current) {
        chartRef.current.applyOptions({
          width: chartContainerRef.current.clientWidth,
        });
      }
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      chart.remove();
    };
  }, []);

  // Update data
  useEffect(() => {
    if (!historicalData || !candlestickSeriesRef.current || !volumeSeriesRef.current) return;

    const candlestickData = historicalData.map((d: HistoricalData) => ({
      time: new Date(d.timestamp).getTime() / 1000,
      open: d.open,
      high: d.high,
      low: d.low,
      close: d.close,
    }));

    const volumeData = historicalData.map((d: HistoricalData) => ({
      time: new Date(d.timestamp).getTime() / 1000,
      value: d.volume,
      color: d.close >= d.open ? '#22c55e80' : '#ef444480',
    }));

    candlestickSeriesRef.current.setData(candlestickData);
    volumeSeriesRef.current.setData(volumeData);
    chartRef.current?.timeScale().fitContent();
  }, [historicalData]);

  if (isLoading) {
    return (
      <div className="h-[400px] flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  return <div ref={chartContainerRef} className="w-full" />;
}
```

### 2. Portfolio Pie Chart

**File:** `src/components/charts/PortfolioChart.tsx`

```tsx
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { PortfolioHolding } from '@/types/portfolio.types';

interface PortfolioChartProps {
  holdings: PortfolioHolding[];
}

const COLORS = [
  '#6366f1', '#8b5cf6', '#a855f7', '#d946ef', 
  '#ec4899', '#f43f5e', '#f97316', '#eab308',
  '#22c55e', '#14b8a6', '#06b6d4', '#3b82f6'
];

export function PortfolioChart({ holdings }: PortfolioChartProps) {
  const data = holdings.map((holding, index) => ({
    name: holding.symbol,
    value: holding.currentValue,
    color: COLORS[index % COLORS.length],
  }));

  const totalValue = data.reduce((sum, item) => sum + item.value, 0);

  const formatTooltip = (value: number) => {
    return `₹${value.toLocaleString('en-IN', { maximumFractionDigits: 2 })} (${((value / totalValue) * 100).toFixed(1)}%)`;
  };

  return (
    <ResponsiveContainer width="100%" height={300}>
      <PieChart>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={100}
          paddingAngle={2}
          dataKey="value"
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={entry.color} />
          ))}
        </Pie>
        <Tooltip 
          formatter={formatTooltip}
          contentStyle={{ 
            backgroundColor: '#1f2937', 
            border: '1px solid #374151',
            borderRadius: '8px',
          }}
          labelStyle={{ color: '#fff' }}
        />
        <Legend 
          verticalAlign="bottom" 
          height={36}
          formatter={(value) => <span className="text-gray-300">{value}</span>}
        />
      </PieChart>
    </ResponsiveContainer>
  );
}
```

---

## 🎨 UI/UX Design Guidelines

### Design Principles

1. **Dark Theme First** - Trading apps work better with dark themes for reduced eye strain during long sessions
2. **Data Density** - Show relevant information without overwhelming users
3. **Color Coding** - Green for profits/buy, Red for losses/sell consistently
4. **Real-time Feedback** - Immediate visual feedback for all actions
5. **Responsive Design** - Works seamlessly on desktop and mobile

### Color Palette

```css
/* Trading Theme Colors */
:root {
  /* Base */
  --background: 0 0% 7%;
  --foreground: 0 0% 98%;
  
  /* Card/Surface */
  --card: 0 0% 12%;
  --card-foreground: 0 0% 98%;
  
  /* Primary - Blue/Purple */
  --primary: 238 84% 67%;
  --primary-foreground: 0 0% 98%;
  
  /* Trading Colors */
  --profit: 142 76% 36%;       /* Green */
  --profit-light: 142 76% 46%;
  --loss: 0 84% 60%;           /* Red */
  --loss-light: 0 84% 70%;
  
  /* Accent */
  --accent: 240 5% 26%;
  --accent-foreground: 0 0% 98%;
  
  /* Borders */
  --border: 240 4% 20%;
  --input: 240 4% 20%;
  
  /* Text */
  --muted: 240 4% 46%;
  --muted-foreground: 240 4% 66%;
}
```

### Typography

```css
/* Font Stack */
--font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
--font-mono: 'JetBrains Mono', 'Fira Code', monospace;

/* Sizes */
--text-xs: 0.75rem;    /* 12px - Labels */
--text-sm: 0.875rem;   /* 14px - Secondary text */
--text-base: 1rem;     /* 16px - Body */
--text-lg: 1.125rem;   /* 18px - Subheadings */
--text-xl: 1.25rem;    /* 20px - Card titles */
--text-2xl: 1.5rem;    /* 24px - Section headers */
--text-3xl: 1.875rem;  /* 30px - Page titles */
--text-4xl: 2.25rem;   /* 36px - Hero text */
```

### Component Examples

**Price Display Component:**
```tsx
function PriceDisplay({ price, change, changePercent }) {
  const isPositive = change >= 0;
  
  return (
    <div className="flex flex-col">
      <span className="text-2xl font-bold text-white font-mono">
        ₹{price.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
      </span>
      <span className={`flex items-center gap-1 text-sm ${
        isPositive ? 'text-green-500' : 'text-red-500'
      }`}>
        {isPositive ? <ArrowUp size={14} /> : <ArrowDown size={14} />}
        {isPositive ? '+' : ''}{change.toFixed(2)} ({changePercent.toFixed(2)}%)
      </span>
    </div>
  );
}
```

---

## 📱 Responsive Design

### Breakpoints

```javascript
// Tailwind breakpoints
const breakpoints = {
  sm: '640px',   // Mobile landscape
  md: '768px',   // Tablet portrait
  lg: '1024px',  // Tablet landscape / Small desktop
  xl: '1280px',  // Desktop
  '2xl': '1536px' // Large desktop
};
```

### Mobile-First Layout

**File:** `src/components/common/Layout.tsx`

```tsx
import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { BottomNav } from './BottomNav';
import { useMediaQuery } from '@/hooks/useMediaQuery';

export function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const isMobile = useMediaQuery('(max-width: 1023px)');

  return (
    <div className="min-h-screen bg-gray-900">
      <Header onMenuClick={() => setSidebarOpen(true)} />
      
      <div className="flex">
        {/* Desktop Sidebar */}
        {!isMobile && (
          <Sidebar className="hidden lg:flex w-64 fixed left-0 top-16 bottom-0" />
        )}
        
        {/* Mobile Sidebar Overlay */}
        {isMobile && sidebarOpen && (
          <>
            <div 
              className="fixed inset-0 bg-black/50 z-40"
              onClick={() => setSidebarOpen(false)}
            />
            <Sidebar 
              className="fixed left-0 top-0 bottom-0 w-64 z-50"
              onClose={() => setSidebarOpen(false)}
            />
          </>
        )}
        
        {/* Main Content */}
        <main className={`flex-1 ${!isMobile ? 'ml-64' : ''} mt-16 ${isMobile ? 'pb-16' : ''}`}>
          <Outlet />
        </main>
      </div>
      
      {/* Mobile Bottom Navigation */}
      {isMobile && <BottomNav />}
    </div>
  );
}
```

### Responsive Grid for Dashboard

```tsx
// Dashboard grid that adapts to screen size
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
  {/* Stats cards */}
</div>

<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
  <div className="lg:col-span-2">
    {/* Chart - takes 2/3 on large screens */}
  </div>
  <div>
    {/* Sidebar content */}
  </div>
</div>
```

---

## 🧪 Testing Strategy

### 1. Unit Tests with Vitest

**File:** `src/components/trading/__tests__/OrderForm.test.tsx`

```tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { OrderForm } from '../OrderForm';

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});

const wrapper = ({ children }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
);

describe('OrderForm', () => {
  it('renders buy and sell tabs', () => {
    render(
      <OrderForm symbol="RELIANCE" market="NSE" currentPrice={2400} />,
      { wrapper }
    );

    expect(screen.getByRole('tab', { name: /buy/i })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: /sell/i })).toBeInTheDocument();
  });

  it('shows limit price field when LIMIT order type selected', async () => {
    const user = userEvent.setup();
    render(
      <OrderForm symbol="RELIANCE" market="NSE" currentPrice={2400} />,
      { wrapper }
    );

    await user.click(screen.getByRole('combobox'));
    await user.click(screen.getByText('Limit'));

    expect(screen.getByLabelText(/limit price/i)).toBeInTheDocument();
  });

  it('calculates estimated value correctly', async () => {
    const user = userEvent.setup();
    render(
      <OrderForm symbol="RELIANCE" market="NSE" currentPrice={2400} />,
      { wrapper }
    );

    const quantityInput = screen.getByLabelText(/quantity/i);
    await user.clear(quantityInput);
    await user.type(quantityInput, '10');

    expect(screen.getByText(/24,000/)).toBeInTheDocument();
  });
});
```

### 2. Integration Tests

**File:** `src/__tests__/integration/trading.test.tsx`

```tsx
import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';
import App from '@/App';

const server = setupServer(
  http.get('/api/stocks/quote/:symbol', () => {
    return HttpResponse.json({
      symbol: 'RELIANCE',
      currentPrice: 2400,
      change: 50,
      changePercent: 2.13,
    });
  }),
  http.post('/api/orders', () => {
    return HttpResponse.json({
      orderId: 'test-order-123',
      status: 'FILLED',
    });
  })
);

beforeAll(() => server.listen());
afterAll(() => server.close());

describe('Trading Flow', () => {
  it('completes a full buy order flow', async () => {
    render(<App />);
    
    // Navigate to trading page
    // Search for stock
    // Place order
    // Verify order confirmation
  });
});
```

### 3. E2E Tests with Playwright

**File:** `e2e/trading.spec.ts`

```typescript
import { test, expect } from '@playwright/test';

test.describe('Trading Platform', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('user can login and place an order', async ({ page }) => {
    // Login
    await page.click('text=Sign In');
    await page.fill('[name="usernameOrEmail"]', 'testuser');
    await page.fill('[name="password"]', 'password123');
    await page.click('button[type="submit"]');

    // Wait for dashboard
    await expect(page).toHaveURL('/dashboard');

    // Navigate to trading
    await page.click('text=Trade');
    
    // Search for stock
    await page.fill('[placeholder="Search stocks"]', 'RELIANCE');
    await page.click('text=RELIANCE');

    // Place order
    await page.fill('[name="quantity"]', '10');
    await page.click('button:has-text("BUY RELIANCE")');

    // Verify success
    await expect(page.locator('text=Order placed successfully')).toBeVisible();
  });
});
```

---

## ⚡ Performance Optimization

### 1. Code Splitting

**File:** `src/App.tsx`

```tsx
import { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { Loading } from '@/components/common/Loading';

// Lazy load pages
const Dashboard = lazy(() => import('@/pages/Dashboard'));
const Trading = lazy(() => import('@/pages/Trading'));
const Portfolio = lazy(() => import('@/pages/Portfolio'));
const Orders = lazy(() => import('@/pages/Orders'));
const Watchlist = lazy(() => import('@/pages/Watchlist'));

function App() {
  return (
    <Suspense fallback={<Loading />}>
      <Routes>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/trading/:symbol?" element={<Trading />} />
        <Route path="/portfolio" element={<Portfolio />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/watchlist" element={<Watchlist />} />
      </Routes>
    </Suspense>
  );
}
```

### 2. React Query Optimization

```typescript
// Query client configuration
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60, // 1 minute
      gcTime: 1000 * 60 * 5, // 5 minutes
      refetchOnWindowFocus: true,
      retry: 2,
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
    },
  },
});
```

### 3. Virtualized Lists for Large Data

```tsx
import { useVirtualizer } from '@tanstack/react-virtual';

function VirtualizedStockList({ stocks }) {
  const parentRef = useRef<HTMLDivElement>(null);

  const virtualizer = useVirtualizer({
    count: stocks.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 60,
    overscan: 5,
  });

  return (
    <div ref={parentRef} className="h-[400px] overflow-auto">
      <div
        style={{
          height: `${virtualizer.getTotalSize()}px`,
          position: 'relative',
        }}
      >
        {virtualizer.getVirtualItems().map((virtualItem) => (
          <div
            key={virtualItem.key}
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: `${virtualItem.size}px`,
              transform: `translateY(${virtualItem.start}px)`,
            }}
          >
            <StockRow stock={stocks[virtualItem.index]} />
          </div>
        ))}
      </div>
    </div>
  );
}
```

### 4. Image & Asset Optimization

```typescript
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-react': ['react', 'react-dom'],
          'vendor-charts': ['lightweight-charts', 'recharts'],
          'vendor-ui': ['@radix-ui/react-dialog', '@radix-ui/react-tabs'],
        },
      },
    },
    chunkSizeWarningLimit: 500,
  },
});
```

---

## 🚀 Deployment

### 1. Build Configuration

**File:** `vite.config.ts`

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true,
      },
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true,
      },
    },
  },
});
```

### 2. Docker Deployment

**File:** `Dockerfile`

```dockerfile
# Build stage
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**File:** `nginx.conf`

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;

    # Cache static assets
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # WebSocket proxy
    location /ws {
        proxy_pass http://backend:8080/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
    }
}
```

### 3. CI/CD with GitHub Actions

**File:** `.github/workflows/deploy.yml`

```yaml
name: Deploy Frontend

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
      - run: npm ci
      - run: npm run lint
      - run: npm run test
      - run: npm run build

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      - name: Build and push Docker image
        run: |
          docker build -t trading-frontend:${{ github.sha }} .
          # Push to container registry
      - name: Deploy to production
        run: |
          # Deploy commands
```

---

## 📅 Development Timeline

### Phase 1: Foundation (Week 1-2)
- [ ] Project setup with Vite + React + TypeScript
- [ ] Configure Tailwind CSS and UI components
- [ ] Set up routing and authentication pages
- [ ] Implement auth store and API integration
- [ ] Create basic layout components

### Phase 2: Core Trading Features (Week 3-4)
- [ ] Dashboard page with portfolio summary
- [ ] Stock search and quote display
- [ ] Trading page with order form
- [ ] Chart integration (TradingView Lightweight)
- [ ] Real-time price updates via WebSocket

### Phase 3: Portfolio & Orders (Week 5-6)
- [ ] Portfolio page with holdings
- [ ] Transaction history
- [ ] Order management (pending, history)
- [ ] Watchlist functionality
- [ ] Price alerts

### Phase 4: Polish & Testing (Week 7-8)
- [ ] Responsive design optimization
- [ ] Unit and integration tests
- [ ] Performance optimization
- [ ] Error handling and loading states
- [ ] User feedback and notifications

### Phase 5: Deployment (Week 9-10)
- [ ] Docker configuration
- [ ] CI/CD pipeline setup
- [ ] Production environment deployment
- [ ] Monitoring and analytics
- [ ] Documentation

---

## 📚 Additional Resources

### Learning Resources
- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [TanStack Query](https://tanstack.com/query/latest)
- [Tailwind CSS](https://tailwindcss.com/docs)
- [TradingView Lightweight Charts](https://tradingview.github.io/lightweight-charts/)

### Design Inspiration
- [Zerodha Kite](https://kite.zerodha.com) - Clean Indian trading UI
- [Robinhood](https://robinhood.com) - Modern, mobile-first design
- [TradingView](https://tradingview.com) - Professional charting
- [Yahoo Finance](https://finance.yahoo.com) - Data-rich layouts

### UI Component Libraries
- [shadcn/ui](https://ui.shadcn.com) - Recommended
- [Radix UI](https://radix-ui.com) - Accessible primitives
- [Headless UI](https://headlessui.com) - Tailwind-focused

---

*This guide provides a comprehensive roadmap for building a professional trading platform frontend that integrates seamlessly with the backend API.*
