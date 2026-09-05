# Ceylinco Logsheet Portal — Technical Overview

**Portal Type**: Mobile-first React web application for Ceylinco sales executives  
**Build Tool**: Vite 5  
**State Management**: React Context + localStorage (no backend API)  
**Styling**: Bootstrap 5 (via react-bootstrap) + custom SCSS

---

## 1. Folder & File Structure

```
executive/
├── package.json                 # Dependencies: react, react-bootstrap, react-router-dom, sass
├── vite.config.js               # Vite configuration
├── index.html                   # Entry point
├── README.md                    # Project documentation
│
├── public/                      # Static assets
│
└── src/
    ├── main.jsx                 # React DOM root; imports app.scss
    ├── App.jsx                  # Router setup (BrowserRouter, Routes)
    │
    ├── context/
    │   ├── AuthContext.jsx      # login() / logout() logic; currentSe, isAuthenticated state
    │   └── DataStoreContext.jsx  # Mock DB: clients, activityLogs, sales CRUD operations
    │
    ├── pages/
    │   ├── LoginPage.jsx        # Form-based login; validates email/phone + password
    │   ├── DashboardPage.jsx    # Home: today's count, follow-ups, recent activities
    │   ├── LogActivityPage.jsx  # Wrapper for ActivityWizard
    │   └── HistoryPage.jsx      # All activities with activity-type & status filters
    │
    ├── components/
    │   ├── layout/
    │   │   ├── AppNavbar.jsx    # Portal header with user badge + logout button
    │   │   └── ProtectedRoute.jsx # Route guard (redirects to login if !isAuthenticated)
    │   │
    │   ├── common/
    │   │   ├── StatusBadge.jsx  # Renders inline badge (Inquired/Pending/Sold/Rejected)
    │   │   └── EmptyState.jsx   # Icon + title + copy placeholder
    │   │
    │   └── wizard/
    │       ├── ActivityWizard.jsx       # Main state machine; handles 6-step flow
    │       ├── StepIndicator.jsx        # Visual progress bar (step 1/N)
    │       ├── StepActivityType.jsx     # Choose activity type (auto-advances)
    │       ├── StepCustomerType.jsx     # New vs. Existing customer (auto-advances)
    │       ├── StepNewCustomerForm.jsx  # Capture new client fields
    │       ├── StepExistingCustomerSearch.jsx # Search & auto-load existing client
    │       ├── StepPolicySelect.jsx     # Accordion of policies by category
    │       ├── StepFollowUp.jsx         # Status picker + conditional fields (follow-up date, premium, rejection reason)
    │       └── StepConfirm.jsx          # Read-only review of all captured data
    │
    ├── hooks/
    │   └── useLocalStorageState.js # Bidirectional localStorage sync for React state
    │
    ├── data/
    │   └── mockData.js            # All lookup tables (ACTIVITY_TYPES, POLICIES, etc.) + seed clients
    │
    └── styles/
        ├── app.scss              # Portal-specific component styling (consumes theme-style.scss tokens)
        └── theme-style.scss      # Ceylinco brand palette & global Bootstrap overrides
```

---

## 2. Routes & Screens

All routes except `/login` are protected by `<ProtectedRoute>` (redirects if not authenticated).

| Route | Component | Purpose |
|-------|-----------|---------|
| `/login` | `LoginPage` | Email/phone + password form; saves authenticated session to localStorage; auto-fill demo account |
| `/` | `DashboardPage` | Home screen: KPI tiles (today, follow-ups, sales), CTA button for new log, due follow-ups list, recent activity feed |
| `/log-activity` | `LogActivityPage` → `ActivityWizard` | Multi-step wizard to log new activity (6 steps: activity type → customer mode → customer details → policy → follow-up status → review) |
| `/history` | `HistoryPage` | Table of all logged activities (currentSe only); filterable by activity type & status |
| `*` (catch-all) | Redirects to `/` | 404 handling |

### Screen Details

**LoginPage**
- Form fields: Email or phone, Password
- Validation: Both fields required; email/phone must match a sales_executive record; password must match DEMO_PASSWORD ("demo123")
- On success: Saves `currentSeId` to localStorage key `"logsheet:currentSeId"`
- Demo quick-fill button pre-fills the first executive account

**DashboardPage**
- Displays: Sales executive's first name ("Hey, {FirstName}")
- KPI Tiles:
  - **Today**: Count of activities logged today by currentSe
  - **Follow-ups**: Count of open follow-ups (next_follow_up_date > today) for currentSe
  - **Sold (mo.)**: Count of sales records (status_id = 3) issued this month by currentSe
- Large CTA button: "Log new activity"
- Two sections:
  1. **Follow-ups due** — List of clients with overdue/due next_follow_up_date (sorted by date); tap to pre-fill wizard with that client (skips customer selection steps)
  2. **Recent activities** — Last 6 activity logs for currentSe (most recent first)

**LogActivityPage**
- Thin wrapper; delegates entirely to `ActivityWizard`
- `ActivityWizard` uses React Router's `useLocation().state` to optionally pre-fill with a client (when launched from dashboard's follow-up list)

**HistoryPage**
- Two dropdowns: Activity Type filter, Lead Status filter (both optional)
- Sorted list of all activities for currentSe, most recent first
- Each row shows: Activity icon, Client name, Activity type · Timestamp · Remarks (if any), Status badge

---

## 3. Data Flow & Storage

### Current Architecture
- **No backend server** — all data is mocked in-memory and persisted to browser localStorage
- **Single source of truth**: Context providers with localStorage hooks
- **Seeded data**: Demo clients, sales executives, lookup tables hardcoded in `mockData.js`

### Context: AuthContext

**State Keys**
- `"logsheet:currentSeId"` — localStorage key storing the authenticated sales executive's ID (or null if not logged in)

**Key Functions**
```javascript
login(identifier, password) → { ok: boolean, error?: string }
  // Finds SALES_EXECUTIVES entry matching (email || phone) + password
  // Sets currentSeId → triggers re-render via useMemo(currentSe)

logout() → void
  // Clears currentSeId from localStorage

useAuth() → { currentSe, login, logout, isAuthenticated }
```

**Demo Accounts** (all password `"demo123"`)
```
id: "se-1"  full_name: "Kasun Perera"        se_level_id: 4 (Senior)
id: "se-2"  full_name: "Tharindu Kulasekara" se_level_id: 3 (Intermediate)
id: "se-3"  full_name: "Prasad Weerasinghe"  se_level_id: 2 (Junior)
id: "se-4"  full_name: "Chathurika Perera"   se_level_id: 1 (Beginner)
```

### Context: DataStoreContext

**Persisted State** (3 localStorage keys)

1. **`"logsheet:clients"`** — Array of client records (seeded with 5 SEED_CLIENTS)
   ```javascript
   {
     id: "cl-1",                    // Generated by makeId("cl")
     full_name: "Nimal Ratnayake",
     address: "45/2 Peradeniya Road, Kandy",
     contact_number: "071 234 5678",
     nic: "198765432V",
     email: "nimal.r@example.com",
     client_type_id: 1,             // FK: CLIENT_TYPES
     financial_level_id: 2,         // FK: FINANCIAL_LEVELS
     lead_source_id: 6,             // FK: LEAD_SOURCES
     rejection_reason_id: null,     // FK: REJECTION_REASONS (set if status_id=4)
     last_policy_id: 11,            // FK: POLICIES (most recent policy discussed)
     created_at: "2026-05-14T09:20:00.000Z",
     updated_at?: "2026-08-31T..."  // Added on updateClient()
   }
   ```

2. **`"logsheet:activityLogs"`** — Array of activity records
   ```javascript
   {
     id: "log-...",                 // Generated by makeId("log")
     se_id: "se-1",                 // FK: SALES_EXECUTIVES
     client_id: "cl-1",             // FK: clients
     activity_type_id: 1,           // FK: ACTIVITY_TYPES
     status_id: 1,                  // FK: LEAD_STATUSES (1=Inquired, 2=Pending, 3=Sold, 4=Rejected)
     activity_date: "2026-08-31T14:30:00.000Z",
     policy_id: 11,                 // FK: POLICIES (what was discussed)
     next_follow_up_date: "2026-09-05",  // ISO date string (null if status ≠ 2)
     remarks: "Very interested, awaiting callback",
     duration_minutes: 15,          // Duration of activity (null if not applicable)
     created_at: "2026-08-31T14:30:00.000Z"
   }
   ```

3. **`"logsheet:sales"`** — Array of sales records (only created when status_id=3)
   ```javascript
   {
     id: "sale-...",                // Generated by makeId("sale")
     client_id: "cl-1",
     policy_id: 11,
     se_id: "se-1",
     issue_date: "2026-08-31",      // ISO date (YYYY-MM-DD)
     renewal_date: null,            // Future renewal date if known
     premium_amount: 25000,         // In LKR
     created_at: "2026-08-31T14:30:00.000Z"
   }
   ```

**Key Queries**
```javascript
searchClients(query) → Client[]
  // Filters by full_name, contact_number, or nic (case-insensitive)
  // Used in StepExistingCustomerSearch

getClientById(clientId) → Client | null
  
addClient(clientData) → Client
  // Generates new id, sets created_at, initializes rejection_reason_id: null
  // Used when wizard captures new customer

updateClient(clientId, patch) → void
  // Merges patch into existing client record
  
addActivityLog(logData) → ActivityLog
  // Generates new id, sets created_at, prepends to array (newest first)
  
getActivitiesForSe(seId) → ActivityLog[]
  // All logs for a given sales executive
  
getActivitiesForClient(clientId) → ActivityLog[]
  // All logs for a given client (sorted newest first)
  
getMostRecentLogsByClient(seId) → ActivityLog[]
  // One log per client (the most recent one) for a given SE
  // Used to determine relationship status without double-counting
  
getOpenFollowUpsForSe(seId) → ActivityLog[]
  // Logs where status_id=2 (Pending) and next_follow_up_date <= today
  
addSale(saleData) → Sale
  // Only called when status_id=3 (Sold) in wizard's handleSave()
```

### Lookup Tables (Hardcoded in mockData.js)

**SE_LEVELS** — Sales executive experience tiers
```javascript
{ id: 1, level_name: "Beginner" }
{ id: 2, level_name: "Junior" }
{ id: 3, level_name: "Intermediate" }
{ id: 4, level_name: "Senior" }
```

**ACTIVITY_TYPES** — Contact methods
```javascript
{ id: 1, activity_name: "Field Visit", icon: "bi-geo-alt-fill" }
{ id: 2, activity_name: "Phone Call", icon: "bi-telephone-fill" }
{ id: 3, activity_name: "WhatsApp / SMS", icon: "bi-whatsapp" }
{ id: 4, activity_name: "Email", icon: "bi-envelope-fill" }
{ id: 5, activity_name: "Meet-up", icon: "bi-people-fill" }
```

**LEAD_SOURCES** — How customer was first found
```javascript
{ id: 1, source_name: "Cold Call" }
{ id: 2, source_name: "Social Media" }
{ id: 3, source_name: "Website" }
{ id: 4, source_name: "Walk-in / Branch Visit" }
{ id: 5, source_name: "NC Counter Visit" }
{ id: 6, source_name: "Referral" }
{ id: 7, source_name: "Renewals" }
{ id: 8, source_name: "Networking / Event" }
```

**REJECTION_REASONS** — Why customer rejected
```javascript
{ id: 1, reason_name: "Price Too High" }
{ id: 2, reason_name: "Bought from Competitor" }
{ id: 3, reason_name: "Not Interested" }
```

**CLIENT_TYPES** — Customer segment
```javascript
{ id: 1, type_name: "Individual" }
{ id: 2, type_name: "Small Business" }
{ id: 3, type_name: "Medium Business" }
{ id: 4, type_name: "Corporate / Enterprise" }
{ id: 5, type_name: "NGO / Non-Profit" }
{ id: 6, type_name: "Government / State-Owned" }
```

**FINANCIAL_LEVELS** — Purchasing power estimate
```javascript
{ id: 1, level_name: "Low" }
{ id: 2, level_name: "Medium" }
{ id: 3, level_name: "High" }
{ id: 4, level_name: "Enterprise" }
```

**LEAD_STATUSES** — Relationship status
```javascript
{ id: 1, status_name: "Inquired" }      // First contact (new customers only)
{ id: 2, status_name: "Pending" }       // Requires next_follow_up_date
{ id: 3, status_name: "Sold" }          // Requires premium_amount; creates sales record
{ id: 4, status_name: "Rejected" }      // Requires rejection_reason_id
```

**POLICY_CATEGORIES** — Insurance categories (4)
```javascript
{ id: 1, category_name: "Niche & Personal General Insurance Schemes" }
{ id: 2, category_name: "Motor Insurance Policies" }
{ id: 3, category_name: "Health & Medical Insurance Policies" }
{ id: 4, category_name: "Commercial & Business Protection Policies" }
```

**POLICIES** — 18 insurance products (grouped by category)
```javascript
{ id: 1, policy_category_id: 1, policy_name: "Ceylinco LIPS Insurance", policy_details: "..." }
{ id: 2, policy_category_id: 1, policy_name: "Ceylinco One Day Cover", policy_details: "..." }
// ... (3–6 per category)
```

### useLocalStorageState Hook

Custom hook for automatic localStorage sync:
```javascript
const [value, setValue] = useLocalStorageState(key, initialValue)
  // On first render: Loads from localStorage[key] or uses initialValue
  // On every value change: Persists to localStorage[key] as JSON
  // Graceful error handling: Logs warning if JSON parse/stringify fails
```

**Usage Example**
```javascript
const [clients, setClients] = useLocalStorageState("logsheet:clients", SEED_CLIENTS)
// Now clients is kept in sync with localStorage automatically
```

---

## 4. React-Bootstrap Components Used Per Screen

### Global (AppNavbar)
- None — uses custom CSS classes (`.portal-navbar`, `.brand-mark`, `.se-avatar`)

### LoginPage
- **Form** — Login form wrapper
- **Form.Group** — Email/phone and password input containers
- **Form.Label** — Input labels
- **Form.Control** — Text input (email/phone) and password input
- **Form.Control.Feedback** — Inline validation error messages

### DashboardPage
- **None** — all custom CSS classes (`.stat-strip`, `.list-card`, `.list-row`, `.cta-log-activity`)

### HistoryPage
- **Form.Select** — Two dropdown filters (activity type, status)

### ActivityWizard & Steps
- **Form.Group**, **Form.Label**, **Form.Control** — Text, select, textarea inputs
- **Form.Select** — Dropdowns (Client Type, Financial Level, Lead Source, etc.)
- **Row**, **Col** — Grid layout for side-by-side fields (new customer form)
- **Form.Control.Feedback** — Validation error messages

### Components (Common)
- None (StatusBadge and EmptyState are custom)

**Utility Classes Used Throughout**
- `d-flex`, `gap-2`, `mb-3` — Flexbox spacing (Bootstrap utility classes)
- `text-secondary`, `text-danger`, `fw-semibold` — Typography utilities
- `d-block`, `d-none`, `d-sm-block` — Responsive display

---

## 5. Theme & Styling Architecture

### Global Style Imports

`src/main.jsx`:
```javascript
import "./styles/app.scss"   // Loads during React root render
```

**Load Order**
1. `theme-style.scss` — Ceylinco brand palette + Bootstrap overrides (loaded first via `@use`)
2. `app.scss` — Portal component styles (consumes theme variables)

### theme-style.scss Structure

**1. Official Ceylinco Palette**
```scss
$ceylinco-gold-yellow:  #FDD900    // Accents, CTAs
$ceylinco-deep-blue:    #004094    // Links, secondary
$ceylinco-alert-red:    #E50019    // Errors, alerts
$ceylinco-dark-gray:    #6F6F6F    // Body text
$ceylinco-pure-black:   #000000    // Headings
$ceylinco-pure-white:   #FFFFFF    // Backgrounds
$ceylinco-light-gray:   #F1F1F1    // Alternating blocks
$ceylinco-footer-dark:  #1C1C1C    // Footer
```

**2. Bootstrap Overrides** (using Sass `@use ... with`)
```scss
@use "bootstrap/scss/bootstrap" with (
  $primary:       $ceylinco-pure-black,
  $secondary:     $ceylinco-gold-yellow,
  $info:          $ceylinco-deep-blue,
  $danger:        $ceylinco-alert-red,
  $body-bg:       $ceylinco-pure-white,
  $body-color:    $ceylinco-dark-gray,
  $link-color:    $ceylinco-deep-blue,
  $link-decoration: none,
);
```

**3. Custom Portal Classes**
- `.navbar-portal` — White nav with black text, gold hover
- `.bg-section-alt` — Light gray alternating block backgrounds
- `.footer-portal` — Dark footer with white headings, gold links
- `.badge-status` — Soft pill badges with 10% opacity backgrounds for status colors

### app.scss Usage

**CSS Variables (`:root`)**
```scss
--ceylinco-gold: #{theme.$ceylinco-gold-yellow}
--ceylinco-blue: #{theme.$ceylinco-deep-blue}
--ceylinco-red:  #{theme.$ceylinco-alert-red}
--ceylinco-green: #1f8a3b
--ceylinco-gray: #{theme.$ceylinco-dark-gray}
--nav-height: 60px
--action-bar-height: 76px
```

**Component-Level SCSS**
- Global headings (`h1`–`h6`): Font-weight 800, tight letter-spacing, pure black
- `.btn`: Font-weight 600, border-radius 10px
- `.btn-gold`: Uses `theme.$ceylinco-gold-yellow`
- `.text-tabular`: Tabular number formatting for amounts

**Per-Component Usage**
- No scoped CSS; all classes are global
- Components use custom class names like `.step-heading`, `.option-card`, `.list-card`, etc.
- All color references ultimately source from `theme-style.scss` variables or CSS custom properties

### Style Application Pattern

**Global** → `main.jsx` imports `app.scss` once at root  
**Component-Level** → Components apply class names from `app.scss` without local `<style>` blocks  
**Shared Tokens** → All components read theme colors from the same SCSS variable namespace

---

## 6. Form Validation Logic

### LoginPage Validation

**Validation Rules**
- `identifier`: Must be non-empty after trim
- `password`: Must be non-empty
- Must match a SALES_EXECUTIVES record by (email || phone) + password
- Error message on mismatch: `"We couldn't match that email/phone and password."`

**State Tracking**
```javascript
const [touched, setTouched] = useState({ identifier: false, password: false })
// Marks field as touched on blur; enables error display only for touched fields
```

**Disabled Submit** → Form not submittable until both fields populated

---

### StepNewCustomerForm Validation

**Required Fields** (must be non-empty)
- `full_name` (trimmed)
- `contact_number` (trimmed)
- `client_type_id` (selected)
- `financial_level_id` (selected)
- `lead_source_id` (selected)

**Optional Fields**
- `address`, `nic`, `email` (no validation; free text)

**Validation Function**
```javascript
export function isNewCustomerFormValid(client) {
  return Boolean(
    client.full_name?.trim() &&
    client.contact_number?.trim() &&
    client.client_type_id &&
    client.financial_level_id &&
    client.lead_source_id
  );
}
```

**Field-Level Sanitization**
- `full_name`: Strips all non-letter, non-space, non-apostrophe/hyphen characters
- `contact_number`: Strips all non-digit characters
- `nic`: No sanitization; accepts any text

**Error Display**
- Only shows errors for fields marked as touched (on blur)
- Bootstrap's `Form.Control.Feedback type="invalid"` renders inline

---

### StepFollowUp Validation

**Conditional Logic by Status**

| Status | Condition | Error |
|--------|-----------|-------|
| **Inquired** (1) | Auto-set for new customers; no validation | N/A |
| **Pending** (2) | Requires `next_follow_up_date` | "Select a follow-up date" |
| **Sold** (3) | Requires `premium_amount` > 0 | "Enter a premium amount greater than 0" |
| **Rejected** (4) | Requires `rejection_reason_id` selected | "Pick a rejection reason" |

**Validation Function**
```javascript
export function isFollowUpStepValid(draft) {
  if (draft.status_id === STATUS.PENDING) {
    return Boolean(draft.next_follow_up_date);
  }
  if (draft.status_id === STATUS.SOLD) {
    return Boolean(draft.premium_amount) && Number(draft.premium_amount) > 0;
  }
  if (draft.status_id === STATUS.REJECTED) {
    return Boolean(draft.rejection_reason_id);
  }
  return false; // Existing customers must pick a status
}
```

**UI Behavior**
- Status picker shows 3 option cards for existing customers (auto-hides for new customers)
- Choosing a status clears unrelated fields (e.g., choosing "Sold" clears next_follow_up_date)
- Conditional fields appear below status picker based on selection

---

### StepExistingCustomerSearch Validation

**Validation Function**
```javascript
export function isExistingCustomerStepValid(draft) {
  return Boolean(draft.selectedClientId);
}
```

- Requires at least one client to be selected from search results
- Search only triggers for queries ≥2 characters
- No typing validation; just a requirement to pick

---

### StepPolicySelect Validation

**Validation Function**
```javascript
export function isPolicyStepValid(draft) {
  return Boolean(draft.policyId);
}
```

- Requires exactly one policy selected (per draft.policyId)
- Error message shown if user tries to advance without selecting

---

### ActivityWizard Step Validity

**Master Validation Router**
```javascript
function getStepValidity(stepKey, draft) {
  case "activityType": return Boolean(draft.activityTypeId);
  case "customerType": return Boolean(draft.customerMode);
  case "customer":     return (new/existing validation);
  case "policy":       return Boolean(draft.policyId);
  case "followup":     return (status-conditional logic);
  case "confirm":      return true;  // Always valid
}
```

**Next Button Disabled** when current step is not valid

---

### History Page Filtering (No Validation)

- Filters are optional; both default to empty string (no filter)
- Changed via `Form.Select` onChange; re-filters immediately
- Supports multi-filter (activity type AND status)

---

## 7. Authentication Handling

### AuthContext Implementation

**Login Flow**
1. User enters email/phone + password on LoginPage
2. `login(identifier, password)` called
3. Matches against SALES_EXECUTIVES list (email.toLowerCase || phone.replace spaces)
4. Password must equal DEMO_PASSWORD ("demo123")
5. On success: Saves `se.id` to localStorage key `"logsheet:currentSeId"`
6. Returns `{ ok: true }` or `{ ok: false, error: "..." }`

**Session Persistence**
- `currentSeId` stored in localStorage under key `"logsheet:currentSeId"`
- On app reload: AuthContext initializes state from localStorage
- Session remains until user clicks logout or clears localStorage

**Logout Flow**
1. `logout()` called
2. Clears `currentSeId` from localStorage
3. Redirects to `/login` via React Router

**Protected Routes**
```javascript
<ProtectedRoute> // Checks isAuthenticated
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <AppNavbar /> + <main>{children}</main>
</ProtectedRoute>
```

**Demo Credentials** (no real auth; hardcoded)
```
Email: kasun.perera@ceylinco.demo  | Level: Senior
Email: tharindu.k@ceylinco.demo    | Level: Intermediate
Email: prasad.w@ceylinco.demo      | Level: Junior
Email: chathurika.p@ceylinco.demo  | Level: Beginner
Password: demo123 (all accounts)
```

**Login Page Features**
- "Autofill it" button pre-fills first demo account + password for quick testing
- Both email and phone number accepted as identifier
- Phone number compared after stripping whitespace (077 123 4561 == 0771234561)
- Case-insensitive email matching

**Mocked Elements** (no real backend)
- No password hashing; plaintext comparison
- No session tokens or JWT
- No server-side validation
- DEMO_PASSWORD hardcoded in source code (mockData.js)

---

## 8. Key Implementation Details

### ActivityWizard Multi-Step Architecture

**Step Sequence Logic**
```javascript
if (draft.skipCustomerSteps) {
  // Pre-filled from dashboard follow-up: skip customer selection
  return ["activityType", "policy", "followup", "confirm"];
} else {
  // New activity log: full flow
  return ["activityType", "customerType", "customer", "policy", "followup", "confirm"];
}
```

**Auto-Advance Steps**
```javascript
const AUTO_ADVANCE_STEPS = new Set(["activityType", "customerType"])
```
- Choosing an activity type → advance to next step automatically
- Choosing new vs. existing → advance automatically
- Other steps require explicit "Next" button click

**Draft State** (internal wizard state)
```javascript
{
  activityTypeId: null,        // 1–5
  customerMode: null,          // "new" | "existing"
  client: null,                // Full client object (new or existing)
  selectedClientId: null,      // ID of selected existing client
  policyId: null,              // Selected policy ID
  status_id: null,             // 1–4 (Inquired/Pending/Sold/Rejected)
  next_follow_up_date: null,   // ISO date string or null
  premium_amount: "",          // String (converted to number on save)
  rejection_reason_id: "",     // ID or empty string
  remarks: "",                 // Free-form notes
  duration_minutes: "",        // String (converted to number on save)
  skipCustomerSteps: boolean   // Pre-filled from dashboard
}
```

**Save Logic** (handleSave)
```javascript
// 1. Create or update client
if (draft.customerMode === "new") {
  // Add new client with all captured fields
  clientId = addClient({ ...draft.client, last_policy_id: draft.policyId }).id;
} else {
  // Update existing client's last_policy_id and rejection_reason_id (if status=Rejected)
  updateClient(draft.selectedClientId, { ... });
}

// 2. Add activity log
addActivityLog({
  se_id: currentSe.id,
  client_id: clientId,
  activity_type_id: draft.activityTypeId,
  status_id: draft.status_id,
  activity_date: new Date().toISOString(),
  policy_id: draft.policyId,
  next_follow_up_date: draft.next_follow_up_date || null,
  remarks: draft.remarks || "",
  duration_minutes: draft.duration_minutes ? Number(...) : null,
});

// 3. If Sold (status_id=3), also create a sales record
if (draft.status_id === 3) {
  addSale({
    client_id: clientId,
    policy_id: draft.policyId,
    se_id: currentSe.id,
    issue_date: new Date().toISOString().slice(0, 10),
    renewal_date: null,
    premium_amount: Number(draft.premium_amount),
  });
}
```

### Dashboard KPI Calculations

**Today's Activity Count**
```javascript
const todayStr = new Date().toISOString().slice(0, 10);
myActivities.filter(log => log.activity_date.slice(0, 10) === todayStr).length
```

**Open Follow-ups**
```javascript
logs where status_id === 2 && next_follow_up_date <= today
```

**This Month's Sales**
```javascript
const monthKey = new Date().toISOString().slice(0, 7);  // "2026-08"
sales.filter(s => s.se_id === currentSe.id && s.issue_date.slice(0, 7) === monthKey).length
```

### Policy Picker Grouping

- Policies grouped by `policy_category_id` in an accordion UI
- Categories are collapsible; clicking a category toggles its open state
- When a policy is selected, its category auto-expands
- Visual checkmark shown next to selected category (when collapsed)

---

## 9. No Backend Integration

The entire app runs client-side:
- **Login** validates against hardcoded SALES_EXECUTIVES list in mockData.js
- **Data storage** uses browser localStorage (not a real database)
- **API calls** — None; all operations are local
- **Network requests** — None beyond initial bundle load

**To connect a real backend**, replace:
1. `AuthContext.login()` → POST /api/auth/login
2. DataStoreContext CRUD methods → Corresponding API calls
3. useLocalStorageState → Replace with Context state or library like Redux
4. mockData.js → Fetch lookup tables from /api/lookups

---

## 10. Summary

| Aspect | Technology |
|--------|-----------|
| **Framework** | React 18 + React Router 6 |
| **UI Library** | React-Bootstrap 2 (Bootstrap 5) |
| **State Management** | React Context + localStorage |
| **Styling** | SCSS (compiled via Sass) |
| **Build** | Vite 5 |
| **Data** | Mocked; persisted to localStorage |
| **Auth** | Hardcoded demo accounts (no backend) |
| **Deployment** | Static site (index.html + bundled JS/CSS) |

**Entry Points**
- User starts at `/login` or is redirected there if not authenticated
- On success, navigates to dashboard (`/`)
- All activity logging routes through the multi-step wizard
- All data persists across page refreshes via localStorage
