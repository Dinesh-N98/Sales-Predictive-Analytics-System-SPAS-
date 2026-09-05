import { useEffect, useState } from "react";

/**
 * Persists React state to localStorage under `key`, seeding it with
 * `initialValue` the first time it's used. This stands in for a real
 * database while the portal has no backend wired up yet.
 */
export function useLocalStorageState(key, initialValue) {
  const [value, setValue] = useState(() => {
    try {
      const stored = window.localStorage.getItem(key);
      return stored ? JSON.parse(stored) : initialValue;
    } catch (err) {
      console.warn(`Could not read localStorage key "${key}"`, err);
      return initialValue;
    }
  });

  useEffect(() => {
    try {
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (err) {
      console.warn(`Could not write localStorage key "${key}"`, err);
    }
  }, [key, value]);

  return [value, setValue];
}
