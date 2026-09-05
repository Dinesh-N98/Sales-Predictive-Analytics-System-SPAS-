# Ceylinco Logsheet Portal

A mobile-first React + React-Bootstrap web portal for Ceylinco sales executives to log
customer activities (field visits, calls, WhatsApp/SMS, emails, meet-ups) as fast as
possible after each interaction.

Built against the two reference files supplied for this project:

- `logsheet_tables.md` — the 18-table database schema. Every dropdown, status, and field
  in this app is pulled from that schema (see **Schema mapping** below).
- `theme-style.scss` — the Ceylinco brand SCSS. Copied in **unmodified** at
  `src/styles/theme-style.scss`; all portal-specific styling lives alongside it in
  `src/styles/app.scss` and only ever reads the brand's color tokens, never overrides
  them.

## Running it

This sandbox has no network access, so the dependencies below couldn't be installed or
build-tested here — install and run it on your own machine:

```bash
npm install
npm run dev
```

Then open the printed local URL (defaults to `http://localhost:5173`) on your phone
(same Wi-Fi network) or in a narrow desktop browser window to see the mobile layout.

### Demo login

There's no backend yet (see below), so login checks against a small in-app list of
sales executives pulled from `sales_executives` in the schema. Every demo account uses
the password `demo123`:

| Email                          | Level        |
| ------------------------------- | ------------ |
| kasun.perera@ceylinco.demo      | Senior       |
| tharindu.k@ceylinco.demo        | Intermediate |
| prasad.w@ceylinco.demo          | Junior       |
| chathurika.p@ceylinco.demo      | Beginner     |

The login screen has an "autofill it" link for the first account.

## What's implemented

- **Login** against the `sales_executives` table (mocked).
- **One activity at a time**, chosen from `activity_types` (Field Visit, Phone Call,
  WhatsApp / SMS, Email, Meet-up).
- **New customer flow** — captures every `clients` field the schema defines
  (`full_name`, `address`, `contact_number`, `nic`, `email`) plus the three lookups
  (`client_type`, `financial_level`, `lead_source`), then the `policy` being pitched.
  A first-contact log is automatically stored with `lead_status = Inquired`.
- **Existing customer flow** — search by name, phone, or NIC; the matching record
  (including their last-discussed policy) loads automatically, nothing is retyped. The
  executive then logs the follow-up with `Pending`, `Sold`, or `Rejected`:
  - `Pending` requires a next follow-up date.
  - `Sold` asks for the premium amount and writes a row to `sales` as well as
    `activity_logs`.
  - `Rejected` requires a reason from `rejection_reasons`.
- **Policy picker** grouped by the four `policy_categories`, pre-filled with the
  customer's last policy of interest when one exists.
- **Dashboard** — today's activity count, open follow-ups, this month's sales count,
  a follow-ups-due list (tapping one jumps straight into the wizard with that customer
  pre-loaded and the customer-selection steps skipped), and a recent-activity feed.
- **History** page with activity-type and status filters.

Data is persisted to `localStorage` so the demo behaves like a real app across
refreshes, without needing a server.

## Schema mapping & assumptions

A few calls were made where the schema left room for interpretation — flagging them
here so they're easy to revisit:

- The schema's `activity_types` table lists **"Field Visit"**; the brief mentioned
  "Random Visit" once. I went with the schema's value since it's the source of truth —
  easy to rename in `src/data/mockData.js` if "Random Visit" (or something else) was
  intended instead.
- `se_levels` had a typo ("Biginer"); displayed as **"Beginner"** in the UI, the
  underlying id order is unchanged.
- The `policies` table has no `policy_category_id` values filled in, but its blank
  separator rows line up exactly with the four `policy_categories` in order — I used
  that grouping (6 / 4 / 3 / 5 policies per category).
- `client_feedbacks` (post-interaction ratings) and `achievements` (target vs. achieved)
  exist in the schema but weren't mentioned in the functional brief, so they're not
  wired into the UI yet — natural next additions.
- New customers' very first log always uses `Inquired`; that status wasn't in the
  brief's `Pending` / `Sold` / `Rejected` list but does exist in `lead_statuses`.

## Project structure

```
src/
  data/mockData.js          Lookup tables + demo seed data (clients, executives)
  context/                  AuthContext (login) + DataStoreContext (mock DB, localStorage)
  components/wizard/        The 4–6 step activity wizard and its per-step screens
  components/layout/        Navbar + auth route guard
  components/common/        StatusBadge, EmptyState
  pages/                    LoginPage, DashboardPage, LogActivityPage, HistoryPage
  styles/
    theme-style.scss        Ceylinco brand file, unmodified
    app.scss                Portal component styles, built on theme-style's tokens
```

## Connecting a real backend

Everything that touches data goes through `src/context/DataStoreContext.jsx` (clients,
activity logs, sales) and `src/context/AuthContext.jsx` (login). To wire up a real API:

1. Replace the `useLocalStorageState` calls in `DataStoreContext.jsx` with data fetched
   from your API on mount (e.g. `useEffect` + `fetch`/your ORM client).
   `addClient`, `updateClient`, `addActivityLog`, and `addSale` are the four writes to
   redirect to real `POST`/`PATCH` calls — their shapes already match the schema's
   column names.
2. Replace `AuthContext.login` with a real auth call, and swap the plaintext
   `DEMO_PASSWORD` check for your identity provider.
3. `src/data/mockData.js` holds every lookup table (`activity_types`, `lead_sources`,
   `policies`, etc.) as static arrays — swap these for API-backed fetches once the
   backend exists, or leave them as-is if these lookups are meant to stay
   rarely-changing/config-driven.

## Next steps worth considering

- Client feedback capture (`client_feedbacks`, `feedback_strengths`,
  `feedback_improvements`) after Field Visit / Meet-up activities.
- An achievements/targets widget on the dashboard (`achievements` table).
- Offline queueing — since this is built for reps in the field, queuing activity logs
  locally and syncing when connectivity returns would be a natural fit once a real API
  is in place.
