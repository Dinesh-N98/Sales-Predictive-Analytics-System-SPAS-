// ==========================================================================
// Mock data layer — mirrors the tables described in logsheet_tables.md
// In production these lookup tables and seed rows would be replaced by real
// API calls (see README "Connecting a real backend").
// ==========================================================================

// --- se_levels ------------------------------------------------------------
export const SE_LEVELS = [
  { id: 1, level_name: "Beginner" },
  { id: 2, level_name: "Junior" },
  { id: 3, level_name: "Intermediate" },
  { id: 4, level_name: "Senior" },
];

// --- sales_executives (demo login accounts only) ---------------------------
// Password is "demo123" for every account — this is a front-end prototype
// with no real authentication backend yet.
export const SALES_EXECUTIVES = [
  { id: "se-1", full_name: "Kasun Perera", email: "kasun.perera@ceylinco.demo", phone_number: "077 123 4561", se_level_id: 4, is_active: true },
  { id: "se-2", full_name: "Tharindu Kulasekara", email: "tharindu.k@ceylinco.demo", phone_number: "077 123 4562", se_level_id: 3, is_active: true },
  { id: "se-3", full_name: "Prasad Weerasinghe", email: "prasad.w@ceylinco.demo", phone_number: "077 123 4563", se_level_id: 2, is_active: true },
  { id: "se-4", full_name: "Chathurika Perera", email: "chathurika.p@ceylinco.demo", phone_number: "077 123 4564", se_level_id: 1, is_active: true },
];

export const DEMO_PASSWORD = "demo123";

// --- activity_types ---------------------------------------------------------
export const ACTIVITY_TYPES = [
  { id: 1, activity_name: "Field Visit", icon: "bi-geo-alt-fill" },
  { id: 2, activity_name: "Phone Call", icon: "bi-telephone-fill" },
  { id: 3, activity_name: "WhatsApp / SMS", icon: "bi-whatsapp" },
  { id: 4, activity_name: "Email", icon: "bi-envelope-fill" },
  { id: 5, activity_name: "Meet-up", icon: "bi-people-fill" },
];

// --- lead_sources -------------------------------------------------------
export const LEAD_SOURCES = [
  { id: 1, source_name: "Cold Call" },
  { id: 2, source_name: "Social Media" },
  { id: 3, source_name: "Website" },
  { id: 4, source_name: "Walk-in / Branch Visit" },
  { id: 5, source_name: "NC Counter Visit" },
  { id: 6, source_name: "Referral" },
  { id: 7, source_name: "Renewals" },
  { id: 8, source_name: "Networking / Event" },
];

// --- rejection_reasons ---------------------------------------------------
export const REJECTION_REASONS = [
  { id: 1, reason_name: "Price Too High" },
  { id: 2, reason_name: "Bought from Competitor" },
  { id: 3, reason_name: "Not Interested" },
];

// --- client_types ---------------------------------------------------------
export const CLIENT_TYPES = [
  { id: 1, type_name: "Individual" },
  { id: 2, type_name: "Small Business" },
  { id: 3, type_name: "Medium Business" },
  { id: 4, type_name: "Corporate / Enterprise" },
  { id: 5, type_name: "NGO / Non-Profit" },
  { id: 6, type_name: "Government / State-Owned" },
];

// --- financial_levels -------------------------------------------------------
export const FINANCIAL_LEVELS = [
  { id: 1, level_name: "Low" },
  { id: 2, level_name: "Medium" },
  { id: 3, level_name: "High" },
  { id: 4, level_name: "Enterprise" },
];

// --- policy_statuses ---------------------------------------------------------
// Used on the `sales` record created once a lead's status becomes "Sold".
export const POLICY_STATUSES = [
  { id: 1, status_name: "Active" },
  { id: 2, status_name: "Expired" },
  { id: 3, status_name: "Renewed" },
  { id: 4, status_name: "Cancelled" },
];

// --- lead_statuses ---------------------------------------------------------
export const LEAD_STATUSES = [
  { id: 1, status_name: "Inquired" },
  { id: 2, status_name: "Pending" },
  { id: 3, status_name: "Sold" },
  { id: 4, status_name: "Rejected" },
];

// --- policy_categories -------------------------------------------------------
export const POLICY_CATEGORIES = [
  { id: 1, category_name: "Niche & Personal General Insurance Schemes" },
  { id: 2, category_name: "Motor Insurance Policies" },
  { id: 3, category_name: "Health & Medical Insurance Policies" },
  { id: 4, category_name: "Commercial & Business Protection Policies" },
];

// --- policies ---------------------------------------------------------------
export const POLICIES = [
  // Category 1 — Niche & Personal General Insurance Schemes
  { id: 1, policy_category_id: 1, policy_name: "Ceylinco LIPS Insurance", policy_details: "Exclusive health, burglary, and accident cover for ladies (Up to Rs. 850,000 benefit)." },
  { id: 2, policy_category_id: 1, policy_name: "Ceylinco One Day Cover", policy_details: "Micro-insurance for individuals against road or home accidents." },
  { id: 3, policy_category_id: 1, policy_name: "Children's Health Policy", policy_details: "Hospitalization and Personal Accident coverage up to Rs. 50,000 for school children." },
  { id: 4, policy_category_id: 1, policy_name: "Ceylinco Dheewara Udana", policy_details: "Dedicated protection plan for fishermen and their families (Up to Rs. 580,000 cover)." },
  { id: 5, policy_category_id: 1, policy_name: "Ceylinco Govi Pawura", policy_details: "Tailored accident and natural disaster cover for Farmers, Teachers, and Housewives." },
  { id: 6, policy_category_id: 1, policy_name: "Ceylinco Doo Daruwo Rakshanaya", policy_details: "Family policy securing children's education if the breadwinner faces illness or death." },
  // Category 2 — Motor Insurance Policies
  { id: 7, policy_category_id: 2, policy_name: "Ceylinco VIP On The Spot (Comprehensive)", policy_details: "Islandwide roadside assistance, plastic surgery cover, 10-year manufacturing defect warranty, and alternative vehicle allocation." },
  { id: 8, policy_category_id: 2, policy_name: "Ceylinco VIP Third Party Cover", policy_details: "Standard third-party liability extended with serious illness medical cost distributions up to Rs. 150,000." },
  { id: 9, policy_category_id: 2, policy_name: "Ceylinco VIP Motor Bike Cover", policy_details: "On-the-spot claim settlements, replacement motorcycle benefits, and personal accident safety parameters." },
  { id: 10, policy_category_id: 2, policy_name: "Ceylinco VIP Three Wheel Cover", policy_details: "Comprehensive 3-wheel safety net covering outstanding lease installments (up to Rs. 100,000) during accident down-time." },
  // Category 3 — Health & Medical Insurance Policies
  { id: 11, policy_category_id: 3, policy_name: "Ceylinco Suwa Sampatha", policy_details: "Surgical and hospitalization expense reimbursement across both private and government healthcare systems." },
  { id: 12, policy_category_id: 3, policy_name: "Ceylinco Suwa Sampatha International", policy_details: "Elite worldwide medical program (excluding the USA) featuring 3 distinct treatment scheme tiers." },
  { id: 13, policy_category_id: 3, policy_name: "Ceylinco Serious Illness Cover", policy_details: "Annual financial cushion up to Rs. 500,000 targeting critical, life-altering conditions." },
  // Category 4 — Commercial & Business Protection Policies
  { id: 14, policy_category_id: 4, policy_name: "Ceylinco Support Line", policy_details: "Total contingency protection for SMEs covering natural disaster recovery and structural income loss." },
  { id: 15, policy_category_id: 4, policy_name: "Burglary & House-Breaking Insurance", policy_details: "Compensation for identified structural assets or contents following forced unlawful entries." },
  { id: 16, policy_category_id: 4, policy_name: "Goods In Transit Insurance", policy_details: "Domestic cargo transit protection coupled with supplementary riot, strike, and terrorism endorsements." },
  { id: 17, policy_category_id: 4, policy_name: "Fidelity Guarantee Insurance", policy_details: "Indemnity safeguarding financial institutions or stores from fraudulent internal employee actions." },
  { id: 18, policy_category_id: 4, policy_name: "Public Liability Insurance", policy_details: "Coverage securing companies against third-party bodily damage or property claims during operations." },
];

// --- seed clients (existing customers, for the "search & auto-load" demo) ---
export const SEED_CLIENTS = [
  {
    id: "cl-1",
    full_name: "Nimal Ratnayake",
    address: "45/2 Peradeniya Road, Kandy",
    contact_number: "071 234 5678",
    nic: "198765432V",
    email: "nimal.r@example.com",
    client_type_id: 1,
    financial_level_id: 2,
    lead_source_id: 6,
    rejection_reason_id: null,
    last_policy_id: 11,
    created_at: "2026-05-14T09:20:00.000Z",
  },
  {
    id: "cl-2",
    full_name: "Green Leaf Traders (Pvt) Ltd",
    address: "12 Colombo Street, Kandy",
    contact_number: "081 222 3344",
    nic: "199234567123",
    email: "accounts@greenleaftraders.demo",
    client_type_id: 2,
    financial_level_id: 3,
    lead_source_id: 4,
    rejection_reason_id: null,
    last_policy_id: 16,
    created_at: "2026-06-02T11:05:00.000Z",
  },
  {
    id: "cl-3",
    full_name: "Kumari Wijesinghe",
    address: "78 Lake Road, Gampola",
    contact_number: "076 555 1122",
    nic: "199512345678",
    email: "kumari.w@example.com",
    client_type_id: 1,
    financial_level_id: 1,
    lead_source_id: 1,
    rejection_reason_id: 1,
    last_policy_id: 2,
    created_at: "2026-06-20T14:40:00.000Z",
  },
  {
    id: "cl-4",
    full_name: "Silva Motors (Pvt) Ltd",
    address: "220 Katugastota Road, Kandy",
    contact_number: "081 233 9988",
    nic: "199845612300",
    email: "info@silvamotors.demo",
    client_type_id: 3,
    financial_level_id: 3,
    lead_source_id: 8,
    rejection_reason_id: null,
    last_policy_id: 7,
    created_at: "2026-06-28T08:15:00.000Z",
  },
  {
    id: "cl-5",
    full_name: "Anoma Jayasuriya",
    address: "9 Temple Lane, Kandy",
    contact_number: "070 987 6543",
    nic: "199011122334",
    email: "anoma.j@example.com",
    client_type_id: 1,
    financial_level_id: 2,
    lead_source_id: 7,
    rejection_reason_id: null,
    last_policy_id: 13,
    created_at: "2026-07-05T10:00:00.000Z",
  },
];

// --- lookup helpers ---------------------------------------------------------
export const findById = (list, id) => list.find((item) => String(item.id) === String(id));
