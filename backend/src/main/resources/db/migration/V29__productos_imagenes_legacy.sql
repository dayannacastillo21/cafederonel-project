-- V29: Imagenes para productos legacy CAF-PROD-* (demo V7/V8 con SKU asignado en V11)

UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1497935586351-8ef259a51875?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-1';  -- Cafe Americano
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1534778108741-bf066870fbf7?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-2';  -- Cappuccino
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1561044623-3d6fbf7f3f1e?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-3';  -- Latte
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1577875671618-a4974348da8f?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-4';  -- Mocaccino
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1559051597-dca253d8c295?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-5';  -- Mocaccino Demo APF02
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1542990253-2591fb0750e5?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-6';  -- Mocaccino Demo APF02 Actualizado
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1556679343-4371039481a0?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-7';  -- Te verde
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1494859802809-ddebfc731cbe?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-8';  -- Croissant
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1553909486-3bc1ff815877?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-9';  -- Sandwich jamon y queso
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1607925962795-3d66c64a2878?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-10'; -- Brownie
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-11'; -- Cheesecake
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1551030173-122dabb23787?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-12'; -- Cafe Americano (duplicado demo)
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-13'; -- Cappuccino Clasico
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1578985545062-c1f58aee1963?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-14'; -- Torta de Chocolate
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1528735603488-26b3d4a1f0e0?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-15'; -- Sandwich Integral
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1460220629363-9e5d79e2eafe?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-16'; -- Latte Vainilla
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1599596152015-7e28b4d7f7b3?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PROD-17'; -- Brownie Artesanal

-- Cualquier otro producto sin imagen util
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=400&h=400&q=80'
WHERE imagen_url IS NULL OR imagen_url = '';
