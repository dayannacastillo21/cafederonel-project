-- V30: Desduplicar imagenes legacy CAF-PROD-* respecto al catalogo CAF-BHC/BFR/SAN

UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1485807263704-65358b7770d6?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-4';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1578317794630-f8fd843a3a3b?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-5';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1587049351349-22a76ae7825d?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-6';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1564890369478-e04c6870ed3b?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-7';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1555507036-ab794f4ada91?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-8';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1619863188248-b3d6e84b0700?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-9';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1510591509093-6d35764b0bbd?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-12';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1476103481722-221fca04841a?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-13';
