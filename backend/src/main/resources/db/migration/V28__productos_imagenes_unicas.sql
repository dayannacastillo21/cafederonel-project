-- V28: Una imagen distinta por producto (Unsplash + Wikimedia Commons, uso libre)
-- Corrige duplicados de V27: croissant/panini, tostada/atun, frappe, etc.

-- Bebidas calientes
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1510591509093-6d35764b0bbd?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-001'; -- Espresso Simple
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1511922069130-de8946ed47d4?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-002'; -- Espresso Doble
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1578915170810-89d5b8e2d397?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-003'; -- Macchiato
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1551030173-122dabb23787?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-004'; -- Americano Grande
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1572442388796-11668a67e53d?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-005'; -- Cappuccino Grande
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-006'; -- Latte Clasico
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1577875671618-a4974348da8f?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-007'; -- Mocha
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1544787213-7190d9d63199?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-008'; -- Flat White
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1542990253-2591fb0750e5?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-009'; -- Chocolate Caliente
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1564890369478-e04c6870ed3b?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-010'; -- Infusion Andina
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1556679343-4371039481a0?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-011'; -- Te Verde
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1476103481722-221fca04841a?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BHC-012'; -- Caramel Macchiato

-- Bebidas frias
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1517701603779-8a75265e3f74?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-001'; -- Cold Brew
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1541167761136-4b3a5cc9d3d8?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-002'; -- Frappe Cafe
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1559051597-dca253d8c295?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-003'; -- Frappe Mocha
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1523672522453-8160c10736fa?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-004'; -- Limonada Hierbabuena
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1621506282392-95865a1ac254?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-005'; -- Jugo Naranja
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1517487888378-60e236815cde?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-006'; -- Iced Latte
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1505252584343-e2a47de0f838?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-007'; -- Smoothie Fresa
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1560493676-2f2e6d254c18?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-BFR-008'; -- Agua Mineral

-- Postres
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1533134242443-854647490a63?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-001'; -- Cheesecake Maracuya
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1519915026518-91297d81538e?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-002'; -- Pie de Limon
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1571877227700-5f39858f6d97?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-003'; -- Tiramisu Cafetero
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1558961363-cf4923918a8a?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-004'; -- Muffin Arandanos
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-005'; -- Galleta Chocochips
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1624355336889-f445494b4e94?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-006'; -- Tres Leches
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Pionono_%28cake%29.jpg/400px-Pionono_%28cake%29.jpg' WHERE sku = 'CAF-POS-007'; -- Pionono
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Alfajores_-_Buenos_Aires%2C_Argentina_%28cropped%29.jpg/400px-Alfajores_-_Buenos_Aires%2C_Argentina_%28cropped%29.jpg' WHERE sku = 'CAF-POS-008'; -- Alfajor
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1551024506-0bccd281d78a?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-009'; -- Donut Glaseada
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1528207776546-e169ac11f4d0?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-POS-010'; -- Panqueque Miel

-- Sandwiches
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1509722745583-ccc64aa74772?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-001'; -- Sandwich Pavo
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1476224203421-9ac5bcbf4e6c?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-002'; -- Sandwich Caprese
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1528735603759-348fc424b0d3?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-003'; -- Panini Pollo
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1623334437517-591e7785a78e?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-004'; -- Croissant Mixto
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1626700051178-ac81a0501240?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-005'; -- Wrap Pollo
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1553909486-3bc1ff815877?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-006'; -- Sandwich Atun
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1568901346515-274646946e88?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-007'; -- Hamburguesa Cafe
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1550317138-9590312e2d88?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SAN-008'; -- Club Sandwich

-- Panaderia
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1494859802809-ddebfc731cbe?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PAN-001'; -- Croissant Mantequilla
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Empanadas_chilenas.jpg/400px-Empanadas_chilenas.jpg' WHERE sku = 'CAF-PAN-002'; -- Empanada Carne
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Empanada_de_pollo.jpg/400px-Empanada_de_pollo.jpg' WHERE sku = 'CAF-PAN-003'; -- Empanada Pollo
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Pan_de_yema.jpg/400px-Pan_de_yema.jpg' WHERE sku = 'CAF-PAN-004'; -- Pan de Yema
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1586440941791-31f659bbf51d?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PAN-005'; -- Pan Integral
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1585477240565-2a8c444c6da2?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PAN-006'; -- Baguette Personal
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1541519227349-84894a3122e2?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PAN-007'; -- Tostada Palta
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1608196831-42df393f97f9?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-PAN-008'; -- Roll Canela

-- Desayunos
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1533089860892-a458c37438db?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-DES-001'; -- Desayuno Americano
UPDATE productos SET imagen_url = 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Tamal_Colorado.JPG/400px-Tamal_Colorado.JPG' WHERE sku = 'CAF-DES-002'; -- Desayuno Criollo
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1488477181941-6429a7be7654?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-DES-003'; -- Bowl Yogurt Granola
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1616197755717-44edf4c24817?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-DES-004'; -- Omelette Queso
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1484723091136-b0cba77f9629?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-DES-005'; -- Tostadas Francesas
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1517673400567-bd99525763eb?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-DES-006'; -- Avena Frutas

-- Ensaladas
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1550304945-86f7d73e2590?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-ENS-001'; -- Ensalada Cesar
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-ENS-002'; -- Ensalada Quinua
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1540189549336-5e1e4e24d627?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-ENS-003'; -- Ensalada Mediterranea
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1546063241-416dea2078f3?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-ENS-004'; -- Bowl Pollo

-- Snacks
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1566471178857-9834136b3491?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SNK-001'; -- Chips Papas
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1599598707969-da8032929c1e?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SNK-002'; -- Mix Frutos Secos
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1606312897040-98cccb304234?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SNK-003'; -- Barra Cereal
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1513456852971-30c0b497ddf8?auto=format&fit=crop&w=400&h=400&q=80' WHERE sku = 'CAF-SNK-004'; -- Nachos Queso

-- Productos demo antiguos (sin SKU CAF-*) — imagen unica por nombre
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1497935586351-8ef259a51875?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) IN ('café americano', 'cafe americano');
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1534778108741-bf066870fbf7?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) IN ('cappuccino', 'cappuccino clasico');
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1494859802809-ddebfc731cbe?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'croissant';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'cheesecake';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1578985545062-c1f58aee1963?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'torta de chocolate';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1528735603488-26b3d4a1f0e0?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'sandwich integral';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1460220629363-9e5d79e2eafe?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'latte vainilla';
UPDATE productos SET imagen_url = 'https://images.unsplash.com/photo-1607925962795-3d66c64a2878?auto=format&fit=crop&w=400&h=400&q=80'
WHERE (sku IS NULL OR sku NOT LIKE 'CAF-%') AND LOWER(nombre) = 'brownie artesanal';
