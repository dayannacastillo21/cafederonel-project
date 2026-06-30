package com.example.backend_cafedronel.service;

import com.example.backend_cafedronel.dto.PosCheckoutRequest;
import com.example.backend_cafedronel.dto.PosCheckoutResponse;

public interface PosService {

    PosCheckoutResponse checkout(PosCheckoutRequest request, String cajeroEmail);
}
