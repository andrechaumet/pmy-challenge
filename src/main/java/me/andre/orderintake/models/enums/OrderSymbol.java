package me.andre.orderintake.models.enums;

import static me.andre.orderintake.models.enums.OrderIndustry.AGRIBUSINESS;
import static me.andre.orderintake.models.enums.OrderIndustry.CONSTRUCTION;
import static me.andre.orderintake.models.enums.OrderIndustry.ENERGY;
import static me.andre.orderintake.models.enums.OrderIndustry.FINANCIAL_MARKETS;
import static me.andre.orderintake.models.enums.OrderIndustry.FINANCIAL_SERVICES;
import static me.andre.orderintake.models.enums.OrderIndustry.HOLDING;
import static me.andre.orderintake.models.enums.OrderIndustry.INDUSTRY;
import static me.andre.orderintake.models.enums.OrderIndustry.OIL_AND_GAS;
import static me.andre.orderintake.models.enums.OrderIndustry.REAL_ESTATE;
import static me.andre.orderintake.models.enums.OrderIndustry.TELECOMMUNICATIONS;

public enum OrderSymbol {
  EDN("Edenor S.A.", ENERGY),
  YPFD("YPF S.A.", OIL_AND_GAS),
  TRAN("Transener S.A.", ENERGY),
  PAMP("Pampa Energía S.A.", ENERGY),
  CEPU("Central Puerto S.A.", ENERGY),
  TXAR("Ternium Argentina S.A.", INDUSTRY),
  BMA("Banco Macro S.A.", FINANCIAL_SERVICES),
  LOMA("Loma Negra C.I.A.S.A.", CONSTRUCTION),
  CRES("Cresud S.A.C.I.F. y A.", AGRIBUSINESS),
  BBAR("BBVA Argentina S.A.", FINANCIAL_SERVICES),
  VALO("Banco de Valores S.A.", FINANCIAL_SERVICES),
  COME("Sociedad Comercial del Plata S.A.", HOLDING),
  MIRG("Grupo Supervielle S.A.", FINANCIAL_SERVICES),
  SUPV("Banco Supervielle S.A.", FINANCIAL_SERVICES),
  TGSU2("Transportadora de Gas del Sur S.A.", ENERGY),
  TECO2("Telecom Argentina S.A.", TELECOMMUNICATIONS),
  TGNO4("Transportadora de Gas del Norte S.A.", ENERGY),
  GGAL("Grupo Financiero Galicia S.A.", FINANCIAL_SERVICES),
  BYMA("Bolsas y Mercados Argentinos S.A.", FINANCIAL_MARKETS),
  IRSA("IRSA Inversiones y Representaciones S.A.", REAL_ESTATE);

  public final String name;
  public final OrderIndustry industry;

  OrderSymbol(String name, OrderIndustry industry) {
    this.name = name;
    this.industry = industry;
  }
}