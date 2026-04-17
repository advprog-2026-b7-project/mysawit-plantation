# MySawit Plantation Service

Service ini jalan di port `8081` dan menggunakan database PostgreSQL terpisah khusus plantation.

## Menjalankan Dengan Infra Repo

1. Jalankan dulu infra repo (yang menyalakan PostgreSQL via Docker).
2. Pastikan database plantation tersedia, default nama DB: `mysawit_plantation`.
3. Jalankan service ini.

## Konfigurasi Database

Service ini sudah membaca konfigurasi datasource dari environment variable, dengan fallback default untuk local:

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5435/mysawit_plantation`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `postgres`)

Jika infra repo expose PostgreSQL di host dan port berbeda, cukup ubah env var tersebut.

## Menjalankan Service

### Local (tanpa Docker)

```bash
./gradlew bootRun
```

### Via Docker Compose

```bash
docker compose up --build
```

## Catatan Skema Plantation

Entity `plantations` pada service ini sudah disejajarkan dengan skema tim:

- `id` (UUID)
- `code` (unique)
- `name`
- `area_hectare`
- `coordinates`
- `assigned_mandor_id`
- `assigned_driver_id`
- `created_at`
- `updated_at`

Fitur update plantation juga sudah menerapkan rule bahwa `code` tidak boleh diubah.
