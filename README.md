### Voucher

* import
    1. saveLog (optional)
    2. convert
    3. createDomain
        1. validate
        2. checkLogic
        3. overrideRepeat
        4. save

* import 重複規則
  * 進/銷 發票：invoice_number、voucher_year_month、十年區間
  * 進/銷 折讓：invoice_number、voucher_year_month、十年區間、allowance_key

[//]: # (interface A)

[//]: # ()
[//]: # (class DomainService :A{)

[//]: # (    )
[//]: # (    void execute&#40;&#41;{})

[//]: # (})

[//]: # ()
[//]: # (class LogAndLockDomainService:A{)

[//]: # ()
[//]: # (    public Lock&#40;A&#41;{)

[//]: # (    )
[//]: # (    })

[//]: # ()
[//]: # (    void execute&#40;&#41;{)

[//]: # (        lock.lock&#40;&#41;)

[//]: # (        log)

[//]: # (    DomainService.execute&#40;&#41;)

[//]: # (        lock.unlock&#40;&#41;)

[//]: # (})

[//]: # (})

