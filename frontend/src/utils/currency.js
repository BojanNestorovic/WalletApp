/**
 * Currency utility for displaying currency codes/acronyms
 */

export const getCurrencySymbol = (currencyName) => {
  const currencyMap = {
    'RSD': 'RSD',
    'EUR': 'EUR',
    'USD': 'USD',
    'GBP': 'GBP',
    'CHF': 'CHF',
    'JPY': 'JPY',
    'CNY': 'CNY',
    'AUD': 'AUD',
    'CAD': 'CAD',
    // Add more as needed
  };
  
  return currencyMap[currencyName] || currencyName;
};

export const formatCurrency = (amount, currency) => {
  const formatted = parseFloat(amount).toFixed(2);
  return `${formatted} ${getCurrencySymbol(currency)}`;
};
